package pl.allegro.techblog.cassandra.domain.service;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import pl.allegro.techblog.cassandra.CassandraIntegrationTest;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.Purchase;
import pl.allegro.techblog.cassandra.domain.model.TagItem;
import pl.allegro.techblog.cassandra.domain.model.User;
import pl.allegro.techblog.cassandra.domain.model.UserItem;
import pl.allegro.techblog.cassandra.domain.model.UserItemKey;
import pl.allegro.techblog.cassandra.domain.repository.ItemRepository;
import pl.allegro.techblog.cassandra.domain.repository.PurchaseRepository;
import pl.allegro.techblog.cassandra.domain.repository.TagItemRepository;
import pl.allegro.techblog.cassandra.domain.repository.UserItemRepository;
import pl.allegro.techblog.cassandra.domain.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@CassandraIntegrationTest
public class ItemServiceIntegrationTest {

    @Inject
    private PurchaseService purchaseService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private UserItemRepository userItemRepository;

    @Inject
    private TagItemRepository tagItemRepository;

    @Inject
    private PurchaseRepository purchaseRepository;

    @Test
    public void shouldUpdateListItemsAfterItemIsBought() throws Exception {
        //given
        UUID userId = UUIDs.timeBased();
        User user = new User(userId, "cassandra_kata", 34);

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
        itemRepository.saveItem(item);

        //when
        int boughtUnits = 2;
        UUID purchaseId = purchaseService.buyNow(itemId, userId, boughtUnits);

        //then
        Purchase foundPurchase = purchaseRepository.findOne(purchaseId);
        Item foundItem = itemRepository.findOne(itemId);
        UserItem foundUserItem = userItemRepository.findOne(new UserItemKey(userId, itemId));
        List<TagItem> foundTagItems = tagItemRepository.findTagItemsByItem(foundItem);
        int availableUnits = offeredUnits - boughtUnits;

        assertThat(foundPurchase.getItemId()).isEqualTo(foundItem.getId());
        assertThat(foundPurchase.getUserId()).isEqualTo(user.getId());
        assertThat(foundPurchase.getQuantity()).isEqualTo(boughtUnits);
        assertThat(foundItem.getAvailableUnits()).isEqualTo(availableUnits);
        assertThat(foundUserItem.getAvailableUnits()).isEqualTo(availableUnits);
        assertThat(foundTagItems).hasSize(item.getTags().size());
        assertThat(foundTagItems).extracting("id.tag").contains("smartphone", "android",
                "htc").doesNotContain("iphone");
        assertThat(foundTagItems).extracting("availableUnits").contains(availableUnits, availableUnits, availableUnits);
    }
}
