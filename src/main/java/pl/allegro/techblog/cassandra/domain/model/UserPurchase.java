package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("cass_user_purchase")
public class UserPurchase {

    @PrimaryKey
    private UserPurchaseKey id;

    @Column("item_id")
    private UUID itemId;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("quantity")
    private Integer quantity;

    @Deprecated
    public UserPurchase() {
    }

    public UserPurchase(UserPurchaseKey id, UUID itemId, BigDecimal unitPrice, Integer quantity) {
        this.id = id;
        this.itemId = itemId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public UserPurchase(UUID userId, UUID purchaseId, UUID itemId, BigDecimal unitPrice, Integer quantity) {
        this(new UserPurchaseKey(userId, purchaseId), itemId, unitPrice, quantity);
    }

    public UserPurchaseKey getId() {
        return id;
    }

    public UUID getItemId() {
        return itemId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
