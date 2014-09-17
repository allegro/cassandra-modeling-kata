package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Table("cass_tag_item")
public class TagItem extends ListingItem {

    @PrimaryKey
    private TagItemKey id;

    @Deprecated
    public TagItem() {
    }

    public TagItem(TagItemKey id, String name, BigDecimal unitPrice, Integer quantity, Date endDate) {
        super(name, unitPrice, quantity, endDate);
        this.id = id;
    }

    public TagItem(String tag, UUID itemId, String name, BigDecimal unitPrice, Integer quantity, Date endDate) {
        this(new TagItemKey(tag, itemId), name, unitPrice, quantity, endDate);
    }

    public TagItemKey getId() {
        return id;
    }

    @Override
    public UUID getItemId() {
        return id.getItemId();
    }
}
