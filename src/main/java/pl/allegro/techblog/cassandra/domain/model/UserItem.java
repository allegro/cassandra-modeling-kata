package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Table("cass_user_item")
public class UserItem extends ListingItem {

    @PrimaryKey
    private UserItemKey id;

    @Deprecated
    public UserItem() {
    }

    public UserItem(UUID userId, UUID itemId, String name, BigDecimal unitPrice, Integer quantity, Date endDate) {
        this(new UserItemKey(userId, itemId), name, unitPrice, quantity, endDate);
    }

    public UserItem(UserItemKey id, String name, BigDecimal unitPrice, Integer quantity, Date endDate) {
        super(name, unitPrice, quantity, endDate);
        this.id = id;
    }

    public UserItemKey getId() {
        return id;
    }

    @Override
    public UUID getItemId() {
        return id.getItemId();
    }
}
