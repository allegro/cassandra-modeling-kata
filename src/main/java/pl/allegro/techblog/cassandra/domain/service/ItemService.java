package pl.allegro.techblog.cassandra.domain.service;

import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.TagItem;
import pl.allegro.techblog.cassandra.domain.model.UserItem;
import pl.allegro.techblog.cassandra.domain.model.UserItemKey;
import pl.allegro.techblog.cassandra.domain.repository.ItemRepository;
import pl.allegro.techblog.cassandra.domain.repository.TagItemRepository;
import pl.allegro.techblog.cassandra.domain.repository.UserItemRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Component
public class ItemService {

    private ItemRepository itemRepository;

    private UserItemRepository userItemRepository;

    private TagItemRepository tagItemRepository;

    @Inject
    public ItemService(ItemRepository itemRepository, UserItemRepository userItemRepository,
                       TagItemRepository tagItemRepository) {
        this.itemRepository = itemRepository;
        this.userItemRepository = userItemRepository;
        this.tagItemRepository = tagItemRepository;
    }

    public void updateListings(UUID itemId) {
        Item item = itemRepository.findOne(itemId);

        updateUserItem(item);
        updateTagItems(item);
    }

    private void updateUserItem(Item item) {
        //TODO: change to updates
        UserItem userItem = userItemRepository.findOne(new UserItemKey(item.getUserId(), item.getId()));
        userItem.setAvailableUnits(item.getAvailableUnits());
        userItem.setFinished(item.getFinished());

        userItemRepository.save(userItem);
    }

    private void updateTagItems(Item item) {
        //TODO: change to updates
        List<TagItem> tagItems = tagItemRepository.findTagItemsByItem(item);
        for (TagItem tagItem : tagItems) {
            tagItem.setAvailableUnits(item.getAvailableUnits());
            tagItem.setFinished(item.getFinished());
        }

        tagItemRepository.save(tagItems);
    }
}
