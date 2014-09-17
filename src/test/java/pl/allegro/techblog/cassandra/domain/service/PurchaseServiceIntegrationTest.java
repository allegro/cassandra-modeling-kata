package pl.allegro.techblog.cassandra.domain.service;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import pl.allegro.techblog.cassandra.CassandraIntegrationTest;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.Purchase;
import pl.allegro.techblog.cassandra.domain.model.User;
import pl.allegro.techblog.cassandra.domain.model.UserPurchase;
import pl.allegro.techblog.cassandra.domain.model.UserPurchaseKey;
import pl.allegro.techblog.cassandra.domain.repository.ItemRepository;
import pl.allegro.techblog.cassandra.domain.repository.PurchaseRepository;
import pl.allegro.techblog.cassandra.domain.repository.UserPurchaseRepository;
import pl.allegro.techblog.cassandra.domain.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@CassandraIntegrationTest
public class PurchaseServiceIntegrationTest {

    @Inject
    private PurchaseService purchaseService;

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private PurchaseRepository purchaseRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserPurchaseRepository userPurchaseRepository;

    @Test
    public void shouldCompletePurchaseAfterItemIsBought() throws Exception {
        //given
        UUID userId = UUIDs.timeBased();
        User user = new User(userId, "cassndra_kata", 34);

        UUID itemId = UUIDs.timeBased();
        String name = "item-1-name";
        String desc = "item-1-desc";
        // TODO: change to DateProvider
        Date startDate = new Date();
        Date endDate = new Date();
        BigDecimal unitPrice = BigDecimal.ONE;
        Integer offeredUnits = 10;
        Set<String> tags = ImmutableSet.of("smartphone", "android", "htc");

        Item item = new Item(itemId, userId, name, desc, unitPrice, offeredUnits, startDate, endDate, tags);

        userRepository.save(user);
        itemRepository.save(item);

        //when
        int boughtUnits = 10;
        UUID purchaseId = purchaseService.buyNow(itemId, userId, boughtUnits);


        //then
        Purchase purchase = purchaseRepository.findOne(purchaseId);
        UserPurchase userPurchase = userPurchaseRepository.findOne(new UserPurchaseKey(userId, purchaseId));

        assertThat(purchase.getItemId()).isEqualTo(itemId);
        assertThat(purchase.getUserId()).isEqualTo(userId);
        assertThat(purchase.getQuantity()).isEqualTo(boughtUnits);
        assertThat(purchase.getStatus()).isEqualTo(Purchase.STATUS_COMPLETED);
        assertThat(userPurchase.getItemId()).isEqualTo(itemId);
        assertThat(userPurchase.getQuantity()).isEqualTo(boughtUnits);
    }
}
