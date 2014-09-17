package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("cass_master_purchase")
public class Purchase {

    public static final String STATUS_ENTERED = "ENTERED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    @PrimaryKey
    private UUID id;

    @Column("item_id")
    private UUID itemId;

    @Column("user_id")
    private UUID userId;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("quantity")
    private Integer quantity;

    @Column("status")
    private String status;

    @Deprecated
    public Purchase() {
    }

    //TODO: constructors can be private
    public Purchase(UUID id, UUID itemId, UUID userId, BigDecimal unitPrice, Integer quantity) {
        this(id, itemId, userId, unitPrice, quantity, STATUS_ENTERED);
    }

    public Purchase(UUID id, UUID itemId, UUID userId, BigDecimal unitPrice, Integer quantity, String status) {
        this.id = id;
        this.itemId = itemId;
        this.userId = userId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getItemId() {
        return itemId;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }
}
