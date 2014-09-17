package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.TagItem;
import pl.allegro.techblog.cassandra.domain.model.TagItemKey;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

public interface TagItemRepository extends TypedIdCassandraRepository<TagItem, TagItemKey>, TagItemRepositoryCustom {
}
