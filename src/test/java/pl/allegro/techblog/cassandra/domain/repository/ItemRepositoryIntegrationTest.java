package pl.allegro.techblog.cassandra.domain.repository;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import pl.allegro.techblog.cassandra.CassandraIntegrationTest;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.TagItem;
import pl.allegro.techblog.cassandra.domain.model.UserItem;
import pl.allegro.techblog.cassandra.domain.model.UserItemKey;
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
public class ItemRepositoryIntegrationTest {

    @Inject
    private ItemRepository itemRepository;

    @Inject
    private UserItemRepository userItemRepository;

    @Inject
    private TagItemRepository tagItemRepository;

    @Test
    public void shouldInsertMasterItemWithDependencies() throws Exception {
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
        Set<String> tags = ImmutableSet.of("smartphone", "android");

        Item item = new Item(itemId, userId, name, desc, unitPrice, offeredUnits, startDate, endDate, tags);

        //when
        itemRepository.saveItem(item);

        //then
        Item foundItem = itemRepository.findOne(itemId);
        assertThat(foundItem.getId()).isEqualTo(itemId);
        assertThat(foundItem.getName()).isEqualTo("item-1-name");
        assertThat(foundItem.getTags()).hasSize(2).contains("smartphone", "android");

        UserItem userItem = userItemRepository.findOne(new UserItemKey(userId, itemId));
        assertThat(userItem.getName()).isEqualTo("item-1-name");

        Iterable<TagItem> tagItems = tagItemRepository.findAll();
        assertThat(tagItems).hasSize(2);
        assertThat(tagItems).extracting("id.tag").contains("smartphone", "android").doesNotContain("apple");
    }
}
