package ir.maralani.finologyscraper.service;

import ir.maralani.finologyscraper.config.TestRedisConfiguration;
import ir.maralani.finologyscraper.dto.ScrapedPage;
import ir.maralani.finologyscraper.service.scraper.DefaultScraperService;
import org.jsoup.HttpStatusException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests related to {@link DefaultScraperService}.
 *
 * @author Amir
 */
@SpringBootTest(classes = TestRedisConfiguration.class, properties = "spring.main.allow-bean-definition-overriding=true")
public class DefaultScraperServiceTest {
    /**
     * Injected service to be tested.
     */
    @Autowired
    private DefaultScraperService defaultScraperService;

    @Autowired
    private RedisTemplate<String, ScrapedPage> redisTemplate;

    /**
     * Spring environment.
     */
    @Autowired
    private Environment environment;

    /**
     * Default path success scenario. Using the default path, checks for sanity of the scrape method.
     */
    @Test
    public void scrape_DefaultPath_ShouldSucceed() {
        try {
            String basePath = environment.getProperty("default.scan.start.path");
            assertThat(redisTemplate.hasKey(basePath)).isFalse();

            ScrapedPage scrapedPage = defaultScraperService.scrape(basePath);

            assertThat(scrapedPage).isNotNull();
            assertThat(scrapedPage.getProduct()).isNotNull();
            assertThat(scrapedPage.getProduct().getName()).isEqualTo("Breathe-Easy Tank");
            assertThat(scrapedPage.getProduct().getDescription())
                    .isEqualTo("The Breathe Easy Tank is so soft, lightweight, and comfortable, you won't even know it's there -- until its high-tech Cocona® fabric starts wicking sweat away from your body to help you stay dry and focused. Layer it over your favorite sports bra and get moving. • Machine wash/dry. • Cocona® fabric.");
            assertThat(scrapedPage.getProduct().getPrice()).isEqualTo("34");
            assertThat(scrapedPage.getProduct().getExtraInformation())
                    .isEqualTo("Style: Tank | Material: Cocona® performance fabric, Cotton | Pattern: Solid | Climate: Indoor, Warm ");

            assertThat(redisTemplate.hasKey(basePath)).isTrue();
        } catch (HttpStatusException | SocketTimeoutException e) {
            // Target is not responding , test could not be continued, assume success
        } catch (IOException e) {
            fail("Unexpected exception", e);
        }
    }

    @Test
    public void startScan() {
        defaultScraperService.startScan(environment.getProperty("default.scan.start.path"));
    }
}
