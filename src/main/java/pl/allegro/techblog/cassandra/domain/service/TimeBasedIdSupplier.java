package pl.allegro.techblog.cassandra.domain.service;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Supplier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TimeBasedIdSupplier implements Supplier<UUID> {

    @Override
    public UUID get() {
        return UUIDs.timeBased();
    }
}
