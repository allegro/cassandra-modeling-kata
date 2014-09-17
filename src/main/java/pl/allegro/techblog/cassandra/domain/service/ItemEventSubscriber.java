package pl.allegro.techblog.cassandra.domain.service;

import com.google.common.eventbus.Subscribe;
import pl.allegro.techblog.cassandra.domain.event.PurchaseBoundEvent;
import pl.allegro.techblog.cassandra.domain.model.Purchase;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class ItemEventSubscriber {

    private ItemService itemService;

    @Inject
    public ItemEventSubscriber(ItemService itemService) {
        this.itemService = itemService;
    }

    @Subscribe
    public void onEvent(PurchaseBoundEvent event) {
        checkNotNull(event, "event cannot be null");
        Purchase purchase = event.getPurchase();

        itemService.updateListings(purchase.getItemId());
    }
}
