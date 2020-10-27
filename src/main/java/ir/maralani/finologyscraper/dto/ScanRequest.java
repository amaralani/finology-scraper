package ir.maralani.finologyscraper.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScanRequest implements Serializable {
    private String path;
}
