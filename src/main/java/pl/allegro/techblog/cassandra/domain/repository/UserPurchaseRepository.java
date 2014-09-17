package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.UserPurchase;
import pl.allegro.techblog.cassandra.domain.model.UserPurchaseKey;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

public interface UserPurchaseRepository extends TypedIdCassandraRepository<UserPurchase, UserPurchaseKey> {
}
