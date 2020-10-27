package ir.maralani.finologyscraper.event.scan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Extension for application event to hold data for each fetched link.
 *
 * @author Amir
 */
@Getter
@Setter
public class ScanEvent extends ApplicationEvent {
    /**
     * Constructor.
     *
     * @param source Source of the event.
     * @param path   Link to be scanned.
     */
    public ScanEvent(Object source, String path) {
        super(source);
        this.path = path;
    }

    private String path;
    private int numberOfRetries;
}
