package pl.allegro.techblog.cassandra.domain.event;

import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class EventPublisher {

    private EventBus eventBus;

    @Inject
    public EventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void publish(Event event) {
        eventBus.post(event);
    }
}
