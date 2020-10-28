package ir.maralani.finologyscraper.service.scraper;

import ir.maralani.finologyscraper.dto.ScrapedPage;
import ir.maralani.finologyscraper.event.scan.ScanEventPublisher;
import ir.maralani.finologyscraper.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * Redis template.
     */
    public final RedisTemplate<String, ScrapedPage> redisTemplate;

    /**
     * Constructor.
     *
     * @param scanEventPublisher Event publisher for scan events.
     * @param redisTemplate      Redis template to interact with Redis.
     */
    public DefaultScraperService(ScanEventPublisher scanEventPublisher,
                                 RedisTemplate<String, ScrapedPage> redisTemplate) {
        this.scanEventPublisher = scanEventPublisher;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Start scanning from the provided path.
     *
     * @param path Path to start from.
     */
    @Override
    public void startScan(String path) {
        scanEventPublisher.publishEvent(path);
    }

    /**
     * Connect to the provided path if it's not already scanned, and extract the data.
     * Also update redis for this path.
     *
     * @param path Provided path to be loaded.
     * @return A {@link ScrapedPage} instance containing the extracted data.
     * @throws IOException If connection to the path is problematic.
     */
    @Override
    public ScrapedPage scrape(String path) throws IOException {
        if (redisTemplate.hasKey(path)) {
            return redisTemplate.opsForValue().get(path);
        }
        Document doc = Jsoup.connect(path).get();
        boolean isProductPage = doc.select(".product-info-main").hasText();
        ScrapedPage scrapedPage = new ScrapedPage();

        if (isProductPage) {
            processProductPage(scrapedPage, doc);
        }
        scrapedPage.setLinks(findAllLinks(doc));
        redisTemplate.opsForValue().set(path, scrapedPage);

        return scrapedPage;
    }

    /**
     * Process a page and retrieve product info from it.
     *
     * @param scrapedPage DTO to be filled.
     * @param doc         Fetched document.
     */
    private void processProductPage(ScrapedPage scrapedPage, Document doc) {
        Product product = new Product();
        product.setPath(doc.location());
        Element mainProductDiv = doc.selectFirst(".product-info-main");

        product.setName(mainProductDiv.selectFirst("[data-ui-id=page-title-wrapper]").text());
        product.setPrice(mainProductDiv.selectFirst(".price-wrapper").attr("data-price-amount"));
        product.setDescription(
                doc.selectFirst(".product.info.detailed .items .item.content .attribute.description .value").text());

        Element extraInfoTable = doc.selectFirst("#product-attribute-specs-table");
        if (extraInfoTable != null) {
            String extraInformation = extraInfoTable.select("tr").stream()
                    .reduce("", (s, element) -> s + element.selectFirst(".col.label").text() + ": " +
                            element.selectFirst(".col.data").text() + " | ", String::concat);
            extraInformation = extraInformation.substring(0, extraInformation.lastIndexOf("|"));
            product.setExtraInformation(extraInformation);
        }

        scrapedPage.setProduct(product);
    }

    /**
     * Retrieve all links from a page.
     * Since using too many filters crashes the site, ignore them. Also ignore anchors.
     *
     * @param document The page to find the links in.
     * @return A set containing links as strings.
     */
    private Set<String> findAllLinks(Document document) {
        // It looks like that too many filters cause the site to crash, so ignore the filters.
        document.select("#layered-filter-block").remove();
        return document.select("a").eachAttr("href").stream()
                .filter(href -> href.startsWith("http")
                        // Ignore anchors
                        && !href.contains("#")
                        && !redisTemplate.hasKey(href))
                .collect(Collectors.toSet());
    }
}
