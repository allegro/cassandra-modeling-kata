package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.Purchase;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

import java.util.UUID;

public interface PurchaseRepository extends TypedIdCassandraRepository<Purchase, UUID>, PurchaseRepositoryCustom {
}
