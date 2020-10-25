package ir.maralani.finologyscraper.event.scan;

import ir.maralani.finologyscraper.dto.ScrapedPage;
import ir.maralani.finologyscraper.service.logger.LoggerService;
import ir.maralani.finologyscraper.service.product.ProductService;
import ir.maralani.finologyscraper.service.scraper.ScraperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Listener for {@link ScanEvent}.
 *
 * @author Amir
 */
@Slf4j
@Component
public class ScanEventListener implements ApplicationListener<ScanEvent> {

    @Autowired
    private ScraperService scraperService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ScanEventPublisher scanEventPublisher;
    @Autowired
    private LoggerService loggerService;

    /**
     * Extract the product information based on the path provided in the event.
     *
     * @param scanEvent Provided event containing the path to be scanned.
     */
    @Override
    public void onApplicationEvent(ScanEvent scanEvent) {
        String path = scanEvent.getPath();
        try {
            ScrapedPage scrapedPage = scraperService.scrape(path);
            synchronized (this) {
                if (!productService.getScannedPages().contains(path)) {
                    productService.save(scrapedPage.getProduct());
                    productService.getScannedPages().add(path);
                    loggerService.log(scrapedPage.getProduct());
                }
                scrapedPage.getRelatedProducts().forEach(relatedPath -> {
                    if (!productService.getScannedPages().contains(relatedPath))
                        scanEventPublisher.publishEvent(relatedPath);
                });
            }
        } catch (IOException e) {
            log.error("Exception during scraping {}", path);
            log.error(e.getMessage());
        }
    }
}
