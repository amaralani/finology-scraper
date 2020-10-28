package ir.maralani.finologyscraper.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Body of a scan request.
 *
 * @author Amir
 */
@Data
public class ScanRequest implements Serializable {
    private String path;
}
