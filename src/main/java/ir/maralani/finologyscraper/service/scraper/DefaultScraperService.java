package ir.maralani.finologyscraper.service.scraper;

import ir.maralani.finologyscraper.dto.ScanJob;
import ir.maralani.finologyscraper.dto.ScrapedPage;
import ir.maralani.finologyscraper.model.Product;
import ir.maralani.finologyscraper.service.logger.LoggerService;
import ir.maralani.finologyscraper.service.product.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
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
     * Redis template.
     */
    private final RedisTemplate<String, ScrapedPage> redisTemplate;

    /**
     * Product Service.
     */
    private final ProductService productService;

    /**
     * Logger Service.
     */
    private final LoggerService loggerService;

    /**
     * All the paths that have been scanned since the application has started.
     */
    private final Set<String> scannedPaths = new HashSet<>();

    /**
     * Blocking queue holding all paths that have to be scanned.
     */
    private final BlockingQueue<ScanJob> queue = new LinkedBlockingDeque<>();

    /**
     * A thread in which the process takes place.
     */
    private Thread processThread = null;

    /**
     * Constructor.
     *
     * @param redisTemplate  Redis template to interact with Redis.
     * @param productService Product service to access and save products.
     * @param loggerService  Logger service.
     */
    public DefaultScraperService(RedisTemplate<String, ScrapedPage> redisTemplate,
                                 ProductService productService,
                                 LoggerService loggerService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
        this.loggerService = loggerService;
    }

    @Override
    public void startScan(String path) {
        // Only run once
        if (processThread == null) {
            processThread = new Thread(() -> {
                try {
                    queue.put(new ScanJob(path, 0));
                    processQueue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            processThread.start();
        }
    }

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
     * @param scrapedPage The DTO to be filled.
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

    /**
     * Get items one by one from {@link #queue} and start process them.
     *
     * @throws InterruptedException If the queue is interrupted while waiting, or the process is interrupted while
     *                              backing off.
     */
    private void processQueue() throws InterruptedException {
        while (true) {
            ScanJob scanJob = queue.take();
            String path = scanJob.getPath();
            try {
                ScrapedPage scrapedPage = scrape(path);

                if (scrapedPage.getProduct() != null && !productService.getScannedPages().contains(path)) {
                    productService.save(scrapedPage.getProduct());
                    productService.getScannedPages().add(path);
                    loggerService.log(scrapedPage.getProduct());
                }

                // Could be handled with streams, but this makes it easier to delegate InterruptedException
                for (String link : scrapedPage.getLinks()) {
                    if (!scannedPaths.contains(link)) {
                        queue.put(new ScanJob(link, 0));
                    }
                }
                scannedPaths.add(path);
            } catch (SocketTimeoutException | HttpStatusException e) {
                if (scanJob.getNumberOfRetries() < 5) {
                    if (e instanceof SocketTimeoutException
                            || (e instanceof HttpStatusException && ((HttpStatusException) e).getStatusCode() > 500)) {
                        // If the target is becoming unresponsive, back off.
                        log.error(e.getMessage());
                        log.error("Target looks unresponsive, backing off for two minutes. Retry count : {}, Path : {}",
                                scanJob.getNumberOfRetries(), scanJob.getPath());
                        try {
                            // Wait for two minutes
                            Thread.sleep(2 * 60 * 1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        } finally {
                            scanJob.setNumberOfRetries(scanJob.getNumberOfRetries() + 1);
                            queue.put(scanJob);
                        }
                    }
                }
            } catch (UnsupportedMimeTypeException ignore) {
                // Tries to download unsupported files (i.e videos), ignore
                scannedPaths.add(path);
            } catch (IOException e) {
                log.error("Exception during scraping {}", path);
                log.error(e.getMessage());
                scannedPaths.add(path);
            }
        }
    }
}
