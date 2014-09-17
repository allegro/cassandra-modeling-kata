package pl.allegro.techblog.cassandra.domain.model;

import com.datastax.driver.core.DataType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.data.cassandra.mapping.CassandraType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Table("cass_master_item")
public class Item {

    @PrimaryKey
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("item_name")
    private String name;

    @Column("item_desc")
    private String description;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("offered_units")
    private Integer offeredUnits;

    @Column("available_units")
    private Integer availableUnits;

    @Column("start_date")
    @CassandraType(type = DataType.Name.TIMESTAMP)
    private Date startDate;

    @Column("end_date")
    @CassandraType(type = DataType.Name.TIMESTAMP)
    private Date endDate;

    @Column("auction_finished")
    private Boolean finished;

    @Column("tags")
    private Set<String> tags;

    @Column("purchases")
    private Map<UUID, Integer> purchases;

    /**
     * @deprecated Only for use by persistence infrastructure
     */
    @Deprecated
    protected Item() {

    }

    public Item(UUID id, UUID userId, String name, String description, BigDecimal unitPrice, Integer offeredUnits,
                Date startDate, Date endDate, Set<String> tags) {
        this(id, userId, name, description, unitPrice, offeredUnits, startDate, endDate, tags, Collections.EMPTY_MAP);
    }

    public Item(UUID id, UUID userId, String name, String description, BigDecimal unitPrice, Integer offeredUnits,
                Date startDate, Date endDate, Set<String> tags, Map<UUID, Integer> purchases) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.offeredUnits = offeredUnits;
        this.availableUnits = offeredUnits;
        //TODO: refactor to Joda Time
        this.startDate = new Date(startDate.getTime());
        this.endDate = new Date(endDate.getTime());
        this.tags = ImmutableSet.copyOf(tags);
        this.purchases = ImmutableMap.copyOf(purchases);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getOfferedUnits() {
        return offeredUnits;
    }

    public Integer getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(Integer availableUnits) {
        this.availableUnits = availableUnits;
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

    public Date getEndDate() {
        return new Date(endDate.getTime());
    }

    public Set<String> getTags() {
        return tags;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Map<UUID, Integer> getPurchases() {
        return purchases;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        final Item other = (Item) obj;
        return Objects.equals(this.id, other.id) && Objects.equals(this.userId, other.userId);
    }
}
