package ir.maralani.finologyscraper.service.logger;

import ir.maralani.finologyscraper.model.Product;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An implementation of {@link CustomLogger} which logs the products in a file.
 *
 * @author Amir
 */
@Slf4j
public class FileLogger implements CustomLogger {
    /**
     * Log file.
     */
    private final File file;

    /**
     * Constructor.
     * File creation is handled in the constructor. Also the old file would be removed.
     *
     * @throws IOException If file can not be created (i.e permission problem).
     */
    public FileLogger() throws IOException {
        this.file = new File("log.txt");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
    }

    @Override
    public void log(Product product) {
        try {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(file.getName(), true));
            out.write(product.toString());
            out.close();
        } catch (IOException e) {
            log.error("Exception when appending to file", e);
        }
    }
}
