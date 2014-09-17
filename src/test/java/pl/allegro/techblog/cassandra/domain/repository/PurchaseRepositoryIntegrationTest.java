package pl.allegro.techblog.cassandra.domain.repository;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import pl.allegro.techblog.cassandra.CassandraIntegrationTest;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.Purchase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(SpringJUnit4ClassRunner.class)
@CassandraIntegrationTest
public class PurchaseRepositoryIntegrationTest {

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private PurchaseRepository purchaseRepository;

    @Test
    public void shouldBindPurchaseToItem() throws Exception {
        //given
        UUID itemId = UUIDs.timeBased();
        UUID userId = UUIDs.timeBased();
        String name = "item-1-name";
        String desc = "item-1-desc";
        // TODO: change to DateProvider
        Date startDate = new Date();
        Date endDate = new Date();
        BigDecimal unitPrice = BigDecimal.ONE;
        Integer offeredUnits = 10;
        Set<String> tags = ImmutableSet.of("smartphone", "android", "htc");

        Item item = new Item(itemId, userId, name, desc, unitPrice, offeredUnits, startDate, endDate, tags);

        UUID purchaseId = UUIDs.timeBased();
        Integer quantity = 2;
        Purchase purchase = new Purchase(purchaseId, itemId, userId, unitPrice, quantity);

        //when
        itemRepository.save(item);
        purchaseRepository.save(purchase);
        boolean bindResult = purchaseRepository.bindPurchaseToItem(purchase, item);

        //then
        assertThat(bindResult).isTrue();
        Item updatedItem = itemRepository.findOne(itemId);
        int availableUnits = offeredUnits - quantity;
        assertThat(updatedItem.getAvailableUnits()).isEqualTo(availableUnits);
        assertThat(updatedItem.getPurchases()).hasSize(1).contains(entry(purchaseId, quantity));
    }
}
