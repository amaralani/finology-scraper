package ir.maralani.finologyscraper.service.logger;

import ir.maralani.finologyscraper.model.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleLogger implements CustomLogger {
    @Override
    public void log(Product product) {
        log.info(product.toString());
    }
}
