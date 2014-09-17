package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.UserItem;
import pl.allegro.techblog.cassandra.domain.model.UserItemKey;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

public interface UserItemRepository extends TypedIdCassandraRepository<UserItem, UserItemKey> {
}
