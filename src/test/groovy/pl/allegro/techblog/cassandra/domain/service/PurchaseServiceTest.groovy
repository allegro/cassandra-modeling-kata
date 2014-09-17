package pl.allegro.techblog.cassandra.domain.service

import com.datastax.driver.core.utils.UUIDs
import pl.allegro.techblog.cassandra.domain.event.EventPublisher
import pl.allegro.techblog.cassandra.domain.event.PurchaseBoundEvent
import pl.allegro.techblog.cassandra.domain.model.Item
import pl.allegro.techblog.cassandra.domain.model.Purchase
import pl.allegro.techblog.cassandra.domain.model.User
import pl.allegro.techblog.cassandra.domain.repository.ItemRepository
import pl.allegro.techblog.cassandra.domain.repository.PurchaseRepository
import pl.allegro.techblog.cassandra.domain.repository.UserRepository
import pl.allegro.techblog.cassandra.domain.service.PurchaseService
import pl.allegro.techblog.cassandra.domain.service.TimeBasedIdSupplier
import spock.lang.Specification

class PurchaseServiceTest extends Specification {

    ItemRepository itemRepository
    UserRepository userRepository
    PurchaseRepository purchaseRepository
    EventPublisher eventPublisher
    TimeBasedIdSupplier idSupplier

    PurchaseService purchaseService

    UUID itemId
    UUID userId
    Item item
    User user

    def setup() {
        itemId = UUID.randomUUID()
        item = Stub(Item) {
            getId() >> itemId
            getUnitPrice() >> BigDecimal.ONE
        }

        userId = UUID.randomUUID()
        user = Stub(User) {
            getId() >> userId
        }

        itemRepository = Stub(ItemRepository) {
            findOne(_) >> item
        }
        userRepository = Stub(UserRepository) {
            findOne(_) >> user
        }
        purchaseRepository = Mock(PurchaseRepository)
        eventPublisher = Mock(EventPublisher)
        idSupplier = Stub(TimeBasedIdSupplier) {
            get() >> UUIDs.timeBased()
        }

        purchaseService = new PurchaseService(itemRepository, userRepository, purchaseRepository, eventPublisher, idSupplier)
    }

    def "should publish event after purchase is bound to item"() {
        when:
        purchaseService.buyNow(itemId, userId, 1)

        then:
        1 * purchaseRepository.savePurchase(_ as Purchase, _)
        1 * purchaseRepository.bindPurchaseToItem(_ as Purchase, _ as Item) >> true
        1 * eventPublisher.publish(_ as PurchaseBoundEvent)
    }

    def "should not publish event is not bound to item"() {
        when:
        purchaseService.buyNow(itemId, userId, 1)

        then:
        1 * purchaseRepository.savePurchase(_ as Purchase, _)
        1 * purchaseRepository.bindPurchaseToItem(_ as Purchase, _ as Item) >> false
        0 * eventPublisher.publish(_ as PurchaseBoundEvent)
    }
}
