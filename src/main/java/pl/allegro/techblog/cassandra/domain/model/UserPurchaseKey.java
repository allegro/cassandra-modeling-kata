package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class UserPurchaseKey implements Serializable {

    private static final long serialVersionUID = 879362415200952400L;

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "purchase_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID purchaseId;

    @Deprecated
    public UserPurchaseKey() {
    }

    public UserPurchaseKey(UUID userId, UUID purchaseId) {
        this.userId = userId;
        this.purchaseId = purchaseId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(UUID purchaseId) {
        this.purchaseId = purchaseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, purchaseId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final UserPurchaseKey other = (UserPurchaseKey) obj;
        return Objects.equals(this.userId, other.userId) && Objects.equals(this.purchaseId, other.purchaseId);
    }
}
