package pl.allegro.techblog.cassandra.infrastructure.persistence;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.Session;
import pl.allegro.techblog.cassandra.domain.model.Item;
import pl.allegro.techblog.cassandra.domain.repository.ItemRepositoryCustom;
import org.springframework.data.cassandra.core.CassandraOperations;

import javax.inject.Inject;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.ttl;

class ItemRepositoryImpl implements ItemRepositoryCustom {

    private static final int TTL_30_DAYS = 2592000;

    private static final String MASTER_ITEM_TABLE = "cass_master_item";
    private static final String USER_ITEM_TABLE = "cass_user_item";
    private static final String TAG_ITEM_TABLE = "cass_tag_item";

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_DESC = "item_desc";
    public static final String UNIT_PRICE = "unit_price";
    public static final String AVAILABLE_UNITS = "available_units";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String TAGS = "tags";
    public static final String ITEM_ID = "item_id";
    public static final String TAG = "tag";

    private CassandraOperations cassandraOperations;

    @Inject
    ItemRepositoryImpl(CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }

    @Override
    public Item saveItem(Item item) {
        cassandraOperations.execute(itemBatch(item));

        return item;
    }

    private BatchStatement itemBatch(Item item) {
        Session session = cassandraOperations.getSession();

        PreparedStatement masterItem = session.prepare(masterItemInsert());
        PreparedStatement userItem = session.prepare(userItemInsertWithTtl());
        PreparedStatement tagItem = session.prepare(tagItemInsertWithTtl());

        BatchStatement batch = new BatchStatement();

        //master item
        batch.add(masterItem.bind(item.getId(), item.getUserId(), item.getName(), item.getDescription(),
                item.getUnitPrice(),
                item.getOfferedUnits(), item.getStartDate(), item.getEndDate(), item.getTags()));
        //user item
        batch.add(userItem.bind(item.getUserId(), item.getId(), item.getName(), item.getUnitPrice(),
                item.getOfferedUnits(), item.getEndDate()));
        //tag items
        for (String tag : item.getTags()) {
            batch.add(tagItem.bind(tag, item.getId(), item.getName(), item.getUnitPrice(),
                    item.getOfferedUnits(), item.getEndDate()));
        }

        return batch;
    }

    private RegularStatement tagItemInsertWithTtl() {
        return insertInto(TAG_ITEM_TABLE)
                .value(TAG, bindMarker())
                .value(ITEM_ID, bindMarker())
                .value(ITEM_NAME, bindMarker())
                .value(UNIT_PRICE, bindMarker())
                .value(AVAILABLE_UNITS, bindMarker())
                .value(END_DATE, bindMarker())
                .using(ttl(TTL_30_DAYS));
    }

    private RegularStatement userItemInsertWithTtl() {
        return insertInto(USER_ITEM_TABLE)
                .value(USER_ID, bindMarker())
                .value(ITEM_ID, bindMarker())
                .value(ITEM_NAME, bindMarker())
                .value(UNIT_PRICE, bindMarker())
                .value(AVAILABLE_UNITS, bindMarker())
                .value(END_DATE, bindMarker())
                .using(ttl(TTL_30_DAYS));
    }

    private RegularStatement masterItemInsert() {
        return insertInto(MASTER_ITEM_TABLE)
                .value(ID, bindMarker())
                .value(USER_ID, bindMarker())
                .value(ITEM_NAME, bindMarker())
                .value(ITEM_DESC, bindMarker())
                .value(UNIT_PRICE, bindMarker())
                .value(AVAILABLE_UNITS, bindMarker())
                .value(START_DATE, bindMarker())
                .value(END_DATE, bindMarker())
                .value(TAGS, bindMarker());
    }
}
