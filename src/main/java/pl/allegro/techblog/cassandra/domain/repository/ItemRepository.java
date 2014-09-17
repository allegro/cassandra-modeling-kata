package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.Item;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

import java.util.UUID;

public interface ItemRepository extends TypedIdCassandraRepository<Item, UUID>, ItemRepositoryCustom {
}
