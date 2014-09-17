package pl.allegro.techblog.cassandra.domain.service;

import pl.allegro.techblog.cassandra.domain.event.EventPublisher;
import pl.allegro.techblog.cassandra.domain.event.PurchaseBoundEvent;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.Purchase;
import pl.allegro.techblog.cassandra.domain.model.User;
import pl.allegro.techblog.cassandra.domain.repository.ItemRepository;
import pl.allegro.techblog.cassandra.domain.repository.PurchaseRepository;
import pl.allegro.techblog.cassandra.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component
public class PurchaseService {

    private static final int TTL_24H = 86400;

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private PurchaseRepository purchaseRepository;

    private EventPublisher eventPublisher;

    private TimeBasedIdSupplier idSupplier;

    @Inject
    public PurchaseService(ItemRepository itemRepository, UserRepository userRepository,
                           PurchaseRepository purchaseRepository, EventPublisher eventPublisher,
                           TimeBasedIdSupplier idSupplier) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.purchaseRepository = purchaseRepository;
        this.eventPublisher = eventPublisher;
        this.idSupplier = idSupplier;
    }

    public UUID buyNow(UUID itemId, UUID userId, int quantity) {
        Item item = itemRepository.findOne(itemId);
        User user = userRepository.findOne(userId);

        UUID purchaseId = idSupplier.get();
        Purchase purchase = new Purchase(purchaseId, item.getId(), user.getId(), item.getUnitPrice(), quantity);
        purchaseRepository.savePurchase(purchase, TTL_24H);

        if (purchaseRepository.bindPurchaseToItem(purchase, item)) {
            eventPublisher.publish(new PurchaseBoundEvent(purchase));
        }

        return purchase.getId();
    }

    public void completePurchase(Purchase purchase) {
        purchaseRepository.updatePurchase(purchase);
    }
}
