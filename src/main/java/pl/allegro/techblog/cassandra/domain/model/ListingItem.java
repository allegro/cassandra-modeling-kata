package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.data.cassandra.mapping.Column;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public abstract class ListingItem {

    @Column("item_name")
    private String name;

    @Column("unit_price")
    private BigDecimal unitPrice;

    @Column("available_units")
    private Integer availableUnits;

    @Column("auction_finished")
    private Boolean finished;

    @Column("end_date")
    private Date endDate;

    @Deprecated
    protected ListingItem() {
    }

    protected ListingItem(String name, BigDecimal unitPrice, Integer availableUnits, Date endDate) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.availableUnits = availableUnits;
        this.endDate = new Date(endDate.getTime());
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(Integer availableUnits) {
        this.availableUnits = availableUnits;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Date getEndDate() {
        return new Date(endDate.getTime());
    }

    public abstract UUID getItemId();
}
