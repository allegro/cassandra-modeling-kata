package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.Item;

public interface ItemRepositoryCustom {

    Item saveItem(Item item);
}
