package ir.maralani.finologyscraper.event.scan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ScanEvent extends ApplicationEvent {
    public ScanEvent(Object source, String path) {
        super(source);
        this.path = path;
    }

    private String path;
}
