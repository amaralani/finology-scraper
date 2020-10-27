package ir.maralani.finologyscraper.service;

import ir.maralani.finologyscraper.dto.ScrapedPage;
import ir.maralani.finologyscraper.service.scraper.DefaultScraperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests related to {@link DefaultScraperService}.
 *
 * @author Amir
 */
@SpringBootTest
public class DefaultScraperServiceTest {
    /**
     * Injected service to be tested.
     */
    @Autowired
    private DefaultScraperService defaultScraperService;

    /**
     * Spring environment.
     */
    @Autowired
    private Environment environment;

    /**
     * Base success scenario. Using the default path, checks for sanity of the scrape method.
     *
     * @throws IOException If default path is inaccessible.
     */
    @Test
    public void scrape_DefaultPath_ShouldSucceed() throws IOException {
        List<String> defaultRelatedProducts = new ArrayList<>();
        defaultRelatedProducts.add("https://magento-test.finology.com.my/mimi-all-purpose-short.html");
        defaultRelatedProducts.add("https://magento-test.finology.com.my/gabrielle-micro-sleeve-top.html");
        defaultRelatedProducts.add("https://magento-test.finology.com.my/ana-running-short.html");
        defaultRelatedProducts.add("https://magento-test.finology.com.my/juliana-short-sleeve-tee.html");

        ScrapedPage scrapedPage = defaultScraperService.scrape(environment.getProperty("default.scan.start.path"));

        assertThat(scrapedPage).isNotNull();
        assertThat(scrapedPage.getProduct()).isNotNull();
        assertThat(scrapedPage.getProduct().getName()).isEqualTo("Breathe-Easy Tank");
        assertThat(scrapedPage.getProduct().getDescription())
                .isEqualTo("The Breathe Easy Tank is so soft, lightweight, and comfortable, you won't even know it's there -- until its high-tech Cocona® fabric starts wicking sweat away from your body to help you stay dry and focused. Layer it over your favorite sports bra and get moving. • Machine wash/dry. • Cocona® fabric.");
        assertThat(scrapedPage.getProduct().getPrice()).isEqualTo("34");
        assertThat(scrapedPage.getProduct().getExtraInformation())
                .isEqualTo("Style: Tank | Material: Cocona® performance fabric, Cotton | Pattern: Solid | Climate: Indoor, Warm ");
        assertThat(scrapedPage.getRelatedProducts()).containsAll(defaultRelatedProducts);
    }

    /**
     * A scenario in which there is no related products provided in the provided path.
     *
     * @throws IOException If the provided path is inaccessible.
     */
    @Test
    public void scrape_JulianaTShirt_ShouldHaveNoRelatedProducts() throws IOException {
        ScrapedPage scrapedPage = defaultScraperService.scrape("https://magento-test.finology.com.my/juliana-short-sleeve-tee.html");

        assertThat(scrapedPage).isNotNull();
        assertThat(scrapedPage.getProduct()).isNotNull();
        assertThat(scrapedPage.getProduct().getName()).isEqualTo("Juliana Short-Sleeve Tee");
        assertThat(scrapedPage.getRelatedProducts()).isEmpty();
    }
}
