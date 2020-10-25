package ir.maralani.finologyscraper.service.logger;

import ir.maralani.finologyscraper.model.Product;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class FileLogger implements CustomLogger {
    private final File file;

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
