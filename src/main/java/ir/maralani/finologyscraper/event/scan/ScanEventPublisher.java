package ir.maralani.finologyscraper.event.scan;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publisher for {@link ScanEvent}.
 *
 * @author Amir
 */
@Component
public class ScanEventPublisher {
    /**
     * Application Event Publisher.
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Constructor.
     *
     * @param applicationEventPublisher Spring Application Event Publisher.
     */
    public ScanEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Create a {@link ScanEvent} from provided path and publish it.
     *
     * @param path The path to scan.
     */
    public void publishEvent(String path) {
        applicationEventPublisher.publishEvent(new ScanEvent(this, path));
    }
}
