package pl.allegro.techblog.cassandra.domain.event;

import pl.allegro.techblog.cassandra.domain.model.Purchase;

public final class PurchaseBoundEvent implements Event {

    private final Purchase purchase;

    public PurchaseBoundEvent(Purchase purchase) {
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
