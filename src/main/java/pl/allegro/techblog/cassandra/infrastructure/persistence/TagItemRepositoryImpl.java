package pl.allegro.techblog.cassandra.infrastructure.persistence;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.TagItem;
import pl.allegro.techblog.cassandra.domain.repository.TagItemRepositoryCustom;
import org.springframework.data.cassandra.core.CassandraOperations;

import javax.inject.Inject;
import java.util.List;

class TagItemRepositoryImpl implements TagItemRepositoryCustom {

    private CassandraOperations cassandraOperations;

    @Inject
    TagItemRepositoryImpl(CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }

    @Override
    public List<TagItem> findTagItemsByItem(Item item) {
        Select select = QueryBuilder.select().from("cass_tag_item");
        select.where(QueryBuilder.in("tag", item.getTags().toArray()))
                .and(QueryBuilder.eq("item_id", item.getId()));

        return cassandraOperations.select(select, TagItem.class);
    }
}
