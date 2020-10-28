package ir.maralani.finologyscraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Extension for application event to hold data for each fetched link.
 *
 * @author Amir
 */
@Data
@AllArgsConstructor
public class ScanJob {
    private String path;
    private int numberOfRetries;
}
