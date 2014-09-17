package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class UserItemKey implements Serializable {

    private static final long serialVersionUID = -791316285695123492L;

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "item_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID itemId;

    @Deprecated
    public UserItemKey() {
    }

    public UserItemKey(UUID userId, UUID itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getItemId() {
        return itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, itemId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        final UserItemKey other = (UserItemKey) obj;
        return Objects.equals(this.userId, other.userId) && Objects.equals(this.itemId, other.itemId);
    }
}
