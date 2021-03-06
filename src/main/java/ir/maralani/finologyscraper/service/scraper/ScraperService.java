package ir.maralani.finologyscraper.service.scraper;

import ir.maralani.finologyscraper.dto.ScrapedPage;

import java.io.IOException;

/**
 * Methods related to scraping and retrieving data.
 *
 * @author Amir
 */
public interface ScraperService {

    /**
     * Start scanning from a given path.
     *
     * @param path Path to start from.
     */
    void startScan(String path);

    /**
     * Scrape the provided path and retrieve the data as a {@link ScrapedPage} DTO.
     *
     * @param path Provided path to be loaded.
     * @return A {@link ScrapedPage} DTO containing product data and related products.
     * @throws IOException If the provided path is inaccessible.
     */
    ScrapedPage scrape(String path) throws IOException;
}
