package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class TagItemKey implements Serializable {
    private static final long serialVersionUID = -674960207411265598L;

    @PrimaryKeyColumn(name = "tag", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String tag;

    @PrimaryKeyColumn(name = "item_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID itemId;

    @Deprecated
    public TagItemKey() {
    }

    public TagItemKey(String tag, UUID itemId) {
        this.tag = tag;
        this.itemId = itemId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, itemId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TagItemKey other = (TagItemKey) obj;
        return Objects.equals(this.tag, other.tag) && Objects.equals(this.itemId, other.itemId);
    }
}
