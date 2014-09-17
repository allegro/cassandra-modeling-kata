package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.User;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

import java.util.UUID;

public interface UserRepository extends TypedIdCassandraRepository<User, UUID> {
}
