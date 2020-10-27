package ir.maralani.finologyscraper.event.scan;

import ir.maralani.finologyscraper.dto.ScrapedPage;
import ir.maralani.finologyscraper.service.logger.LoggerService;
import ir.maralani.finologyscraper.service.product.ProductService;
import ir.maralani.finologyscraper.service.scraper.ScraperService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

/**
 * Listener for {@link ScanEvent}.
 *
 * @author Amir
 */
@Slf4j
@Component
public class ScanEventListener implements ApplicationListener<ScanEvent> {

    /**
     * Scraper Service.
     */
    private final ScraperService scraperService;

    /**
     * Product service.
     */
    private final ProductService productService;

    /**
     * Event publisher.
     */
    private final ScanEventPublisher scanEventPublisher;

    /**
     * Logger Service.
     */
    private final LoggerService loggerService;

    /**
     * All the paths that have been scanned since the application has started.
     */
    public Set<String> scannedPaths = new HashSet<>();

    /**
     * Constructor.
     *
     * @param scraperService     Scraper Service to handle scraping methods.
     * @param productService     Product Service to handle save and retrieval of products.
     * @param scanEventPublisher Event publisher to publish event for new paths.
     * @param loggerService      Logging service to log the new products.
     */
    public ScanEventListener(ScraperService scraperService,
                             ProductService productService,
                             ScanEventPublisher scanEventPublisher,
                             LoggerService loggerService) {
        this.scraperService = scraperService;
        this.productService = productService;
        this.scanEventPublisher = scanEventPublisher;
        this.loggerService = loggerService;
    }

    /**
     * Extract the product information based on the path provided in the event.
     * In case of timeout ({@link SocketTimeoutException}) or server error (HTTP status 5xx) retry up to 5 times.
     * Ignores the paths with non-supported headers.
     *
     * @param scanEvent Provided event containing the path to be scanned.
     */
    @Override
    public void onApplicationEvent(ScanEvent scanEvent) {
        String path = scanEvent.getPath();
        try {
            ScrapedPage scrapedPage = scraperService.scrape(path);
            synchronized (this) {
                if (scrapedPage.getProduct() != null && !productService.getScannedPages().contains(path)) {
                    productService.save(scrapedPage.getProduct());
                    productService.getScannedPages().add(path);
                    loggerService.log(scrapedPage.getProduct());
                }
                scrapedPage.getLinks().stream()
                        .filter(link -> !scannedPaths.contains(link))
                        .forEach(scanEventPublisher::publishEvent);
                scannedPaths.add(path);
            }
        } catch (SocketTimeoutException | HttpStatusException e) {
            if (scanEvent.getNumberOfRetries() < 5) {
                if (e instanceof SocketTimeoutException
                        || (e instanceof HttpStatusException && ((HttpStatusException) e).getStatusCode() > 500)) {
                    // If the target is becoming unresponsive, back off.
                    log.error(e.getMessage());
                    log.error("Target looks unresponsive, backing off for one minute. Retry count : {}, Path : {}",
                            scanEvent.getNumberOfRetries(), scanEvent.getPath());
                    try {
                        // Wait for two minutes
                        Thread.sleep(2 * 60 * 1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } finally {
                        scanEvent.setNumberOfRetries(scanEvent.getNumberOfRetries() + 1);
                        scanEventPublisher.publishEvent(scanEvent);
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
