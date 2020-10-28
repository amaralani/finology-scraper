package ir.maralani.finologyscraper.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Extension for application event to hold data for each fetched link.
 *
 * @author Amir
 */
@Getter
@Setter
public class ScanJob {

    /**
     * Constructor.
     *
     * @param path Link to be scanned.
     */
    public ScanJob(String path) {
        this.path = path;
    }

    private String path;
    private int numberOfRetries;
}
