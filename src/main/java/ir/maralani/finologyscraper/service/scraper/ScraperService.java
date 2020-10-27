package ir.maralani.finologyscraper.service.scraper;

import ir.maralani.finologyscraper.dto.ScrapedPage;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;

/**
 * Methods related to scraping and retrieving data.
 *
 * @author Amir
 */
public interface ScraperService {

    @Async
    void scanPath(String path);

    /**
     * Scrape the provided path and retrieve the data as a {@link ScrapedPage} DTO.
     *
     * @param path Provided path to be loaded.
     * @return A {@link ScrapedPage} DTO containing product data and related products.
     * @throws IOException If the provided path is inaccessible.
     */
    ScrapedPage scrape(String path) throws IOException;
}
