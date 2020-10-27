package ir.maralani.finologyscraper.controller;

import ir.maralani.finologyscraper.dto.ProductListResponse;
import ir.maralani.finologyscraper.dto.ScanRequest;
import ir.maralani.finologyscraper.service.product.ProductService;
import ir.maralani.finologyscraper.service.scraper.ScraperService;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MainController {

    /**
     * Scraper Service.
     */
    private final ScraperService scraperService;

    /**
     * Product Service.
     */
    private final ProductService productService;

    /**
     * Spring Environment.
     */
    private final Environment environment;

    /**
     * Constructor.
     *
     * @param scraperService Service to handle scrape methods.
     * @param productService Service to handle product related methods.
     * @param environment    Spring environment.
     */
    public MainController(ScraperService scraperService, ProductService productService, Environment environment) {
        this.scraperService = scraperService;
        this.productService = productService;
        this.environment = environment;
    }

    /**
     * Endpoint to start scanning the products.
     *
     * @param scanRequest Request body, which may contain a base path.
     * @return {@link org.springframework.http.HttpStatus#ACCEPTED} if request is processable.
     */
    @PostMapping(value = "scan", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> startScan(@RequestBody ScanRequest scanRequest) {
        if (scanRequest.getPath() != null) {
            scraperService.scanPath(scanRequest.getPath());
        } else {
            scraperService.scanPath(environment.getProperty("default.scan.start.path"));
        }
        // Non-committal response indicating that the process is started
        return ResponseEntity.accepted().build();
    }

    /**
     * Controller method to return all products currently inserted in the database.
     *
     * @return A {@link ProductListResponse} containing current product data.
     */
    @GetMapping(value = "product/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductListResponse> getAllFetchedProducts() {
        return ResponseEntity.ok(new ProductListResponse().fromProductList(productService.getAllProducts()));
    }
}
