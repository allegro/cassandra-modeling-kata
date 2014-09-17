package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.TagItem;

import java.util.List;

public interface TagItemRepositoryCustom {

    List<TagItem> findTagItemsByItem(Item item);
}
