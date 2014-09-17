package pl.allegro.techblog.cassandra.domain.repository;

import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.Purchase;

public interface PurchaseRepositoryCustom {

    void savePurchase(Purchase purchase, int ttl);

    void updatePurchase(Purchase purchase);

    boolean bindPurchaseToItem(Purchase purchase, Item item);
}
