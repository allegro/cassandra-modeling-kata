package pl.allegro.techblog.cassandra.domain.service;

import com.google.common.eventbus.Subscribe;
import pl.allegro.techblog.cassandra.domain.event.PurchaseBoundEvent;
import pl.allegro.techblog.cassandra.domain.model.Purchase;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class PurchaseEventSubscriber {

    private PurchaseService purchaseService;

    @Inject
    public PurchaseEventSubscriber(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Subscribe
    public void onEvent(PurchaseBoundEvent event) {
        checkNotNull(event, "event cannot be null");
        Purchase purchase = event.getPurchase();

        purchaseService.completePurchase(purchase);
    }
}
