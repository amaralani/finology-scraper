package ir.maralani.finologyscraper.service.scraper;

import ir.maralani.finologyscraper.dto.ScrapedPage;
import ir.maralani.finologyscraper.event.scan.ScanEventPublisher;
import ir.maralani.finologyscraper.model.Product;
import ir.maralani.finologyscraper.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of {@link ScraperService}.
 *
 * @author Amir
 */
@Service
@Slf4j
public class DefaultScraperService implements ScraperService {

    /**
     * Scan Event Publisher.
     */
    public final ScanEventPublisher scanEventPublisher;

    public final RedisTemplate<String, ScrapedPage> redisTemplate;

    public final ProductService productService;

    public DefaultScraperService(ScanEventPublisher scanEventPublisher, RedisTemplate<String, ScrapedPage> redisTemplate, ProductService productService) {
        this.scanEventPublisher = scanEventPublisher;
        this.redisTemplate = redisTemplate;
        this.productService = productService;
    }

    @Override
    public void scanPath(String path) {
        scanEventPublisher.publishEvent(path);
    }

    @Override
    public ScrapedPage scrape(String path) throws IOException {
        if (redisTemplate.hasKey(path)) {
            return redisTemplate.opsForValue().get(path);
        }
        log.error(path);
        Document doc = Jsoup.connect(path).get();
        Product product = new Product();
        Element mainProductDiv = doc.selectFirst(".product-info-main");
        product.setName(mainProductDiv.selectFirst("[data-ui-id=page-title-wrapper]").text());
        product.setPrice(Float.valueOf(mainProductDiv.selectFirst(".price-wrapper").attr("data-price-amount")));
        product.setDescription(doc.selectFirst(".product.info.detailed .items .item.content .attribute.description .value").text());

        Element extraInfoTable = doc.selectFirst("#product-attribute-specs-table");
        String extraInformation = extraInfoTable.select("tr").stream()
                .reduce("", (s, element) -> s + element.selectFirst(".col.label").text() + ": " + element.selectFirst(".col.data").text() + " | ", String::concat);
        extraInformation = extraInformation.substring(0, extraInformation.lastIndexOf("|"));
        product.setExtraInformation(extraInformation);

        ScrapedPage scrapedPage = new ScrapedPage();
        scrapedPage.setProduct(product);
        scrapedPage.setRelatedProducts(doc.select(".related .product-item-info a.product-item-link").eachAttr("href"));

        redisTemplate.opsForValue().set(path, scrapedPage);

        return scrapedPage;
    }
}
