package pl.allegro.techblog.cassandra.infrastructure.persistence;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.model.Purchase;
import pl.allegro.techblog.cassandra.domain.repository.PurchaseRepositoryCustom;
import org.springframework.cassandra.core.SessionCallback;
import org.springframework.data.cassandra.core.CassandraOperations;

import javax.inject.Inject;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.put;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;
import static com.datastax.driver.core.querybuilder.QueryBuilder.ttl;
import static com.datastax.driver.core.querybuilder.QueryBuilder.update;

class PurchaseRepositoryImpl implements PurchaseRepositoryCustom {

    private static final String APPLIED = "[applied]";

    public static final String MASTER_ITEM_TABLE = "cass_master_item";
    public static final String AVAILABLE_UNITS = "available_units";
    public static final String AUCTION_FINISHED = "auction_finished";
    public static final String PURCHASES = "purchases";
    public static final String ID = "id";
    public static final String MASTER_PURCHASE_TABLE = "cass_master_purchase";
    public static final String ITEM_ID = "item_id";
    public static final String USER_ID = "user_id";
    public static final String UNIT_PRICE = "unit_price";
    public static final String QUANTITY = "quantity";
    public static final String STATUS = "status";
    public static final String PURCHASE_ID = "purchase_id";
    public static final String USER_PURCHASE_TABLE = "cass_user_purchase";

    private CassandraOperations cassandraOperations;

    @Inject
    PurchaseRepositoryImpl(CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }

    @Override
    public void savePurchase(Purchase purchase, int ttl) {
        Session session = cassandraOperations.getSession();
        PreparedStatement masterPurchase = session.prepare(purchaseInsertWithTtl());

        cassandraOperations.execute(masterPurchase.bind(purchase.getId(), purchase.getItemId(), purchase.getUserId(),
                purchase.getUnitPrice(), purchase.getQuantity(), ttl));
    }

    @Override
    public void updatePurchase(Purchase purchase) {
        cassandraOperations.execute(purchaseBatch(purchase));
    }

    private BatchStatement purchaseBatch(Purchase purchase) {
        Session session = cassandraOperations.getSession();

        PreparedStatement purchaseStatusUpdate = session.prepare(purchaseStatusUpdate());
        PreparedStatement userPurchaseInsert = session.prepare(userPurchaseInsert());

        BatchStatement batch = new BatchStatement();

        batch.add(purchaseStatusUpdate.bind(Purchase.STATUS_COMPLETED, purchase.getId()));
        batch.add(userPurchaseInsert.bind(purchase.getUserId(), purchase.getId(), purchase.getItemId(),
                purchase.getUnitPrice(), purchase.getQuantity()));

        return batch;
    }

    private RegularStatement userPurchaseInsert() {
        return insertInto(USER_PURCHASE_TABLE)
                .value(USER_ID, bindMarker())
                .value(PURCHASE_ID, bindMarker())
                .value(ITEM_ID, bindMarker())
                .value(UNIT_PRICE, bindMarker())
                .value(QUANTITY, bindMarker());
    }

    private RegularStatement purchaseStatusUpdate() {
        return update(MASTER_PURCHASE_TABLE)
                .with(set(STATUS, bindMarker()))
                .where(eq(ID, bindMarker()));
    }

    private RegularStatement purchaseInsertWithTtl() {
        return insertInto(MASTER_PURCHASE_TABLE)
                .value(ID, bindMarker())
                .value(ITEM_ID, bindMarker())
                .value(USER_ID, bindMarker())
                .value(UNIT_PRICE, bindMarker())
                .value(QUANTITY, bindMarker())
                .using(ttl(bindMarker()));
    }

    @Override
    public boolean bindPurchaseToItem(Purchase purchase, Item item) {
        return executeUpdate(bindPurchaseUpdateTx(purchase, item));
    }

    private boolean executeUpdate(final RegularStatement bindPurchaseUpdate) {
        return cassandraOperations.execute(new SessionCallback<Boolean>() {
            @Override
            public Boolean doInSession(Session session) {
                ResultSet resultSet = session.execute(bindPurchaseUpdate);
                return resultSet.one().getBool(APPLIED);
            }
        });
    }

    private RegularStatement bindPurchaseUpdateTx(Purchase purchase, Item item) {
        int availableUnitsBefore = item.getAvailableUnits();
        int availableUnitsAfter = availableUnitsBefore - purchase.getQuantity();
        boolean auctionFinished = availableUnitsAfter == 0;

        //update the item with "optimistic locking" - lightweight transactions UPDATE..IF
        return update(MASTER_ITEM_TABLE)
                .with(set(AVAILABLE_UNITS, availableUnitsAfter))
                .and(set(AUCTION_FINISHED, auctionFinished))
                .and(put(PURCHASES, purchase.getId(), purchase.getQuantity()))
                .where(eq(ID, item.getId()))
                .onlyIf(eq(AVAILABLE_UNITS, availableUnitsBefore));
    }
}
