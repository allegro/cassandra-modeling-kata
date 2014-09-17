# Cassandra Modeling Kata

## What is Apache Cassandra?

Apache Cassandra is an open source, distributed, high performance, cloud-friendly NoSQL database offering high availability with its masterless and no single-point-of-failure architecture. 
If you are new to Apache Cassandra and NoSQL, I highly recommend reading this great introductions: 

* [What is Apache Cassandra](http://planetcassandra.org/what-is-apache-cassandra/)
* [NoSQL Databases Defined and Explained](http://planetcassandra.org/what-is-nosql/)

At Allegro we operate in a highly distributed (Microservices), data-intensive cloud environment and Apache Cassandra is a great asset in our [PolyglotPersistence](http://martinfowler.com/bliki/PolyglotPersistence.html) toolbox. 
Apache Cassandra is becoming our No. 1 choice for cloud-based solutions due to its high availability, linear scaling and flexible data modeling capabilities. 

In this kata, I will introduce basic Apache Cassandra modeling techniques. Next, we will use these techniques in practice - to develop a simple e-commerce application.


## Data Modeling in a Nutshell

Apache Cassandra is a [Column store](http://planetcassandra.org/what-is-nosql/#nosql-database-types). Initially, Apache Cassandra offered only the [Thrift](http://wiki.apache.org/cassandra/ThriftInterface) client API. 
Since version 1.2 Apache Cassandra provides [CQL](http://wiki.apache.org/cassandra/API) - SQL-like query abstraction layer. The preferred interface to interact with Cassandra in versions 1.2 and 2.x is CQL.

In this kata, we will use **DataStax Java Driver version 2.x** - all the examples are in CQL3. However, to effectively use CQL, it is crucial to understand how CQL maps to the internal
Cassandra storage - I highly recommend watching this webinar: [Understanding How CQL3 Maps to Cassandra's Internal Data Structure](https://www.youtube.com/watch?v=UP74jC1kM3w).

After getting familiar with the mapping of CQL to the Cassandra internal storage, the next step should be reading/watching **Patrick McFadin's data modeling series**:

* [The Data Model is Dead; Long live the Data Model](http://www.slideshare.net/patrickmcfadin/the-data-model-is-dead-long-live-the-data-model)
* [Become a Super Modeler](http://www.slideshare.net/patrickmcfadin/become-a-super-modeler)
* [The World's Next Top Data Model](http://www.slideshare.net/planetcassandra/c-summit-2013-the-worlds-next-top-data-model-by-patrick-mcfadin)
* [Apache Cassandra 2.0: Data Model on Fire](http://www.slideshare.net/DataStax/cassandra-community-webinar-data-model-on-fire)

Now, before we jump into coding, let's sum up the Cassandra modeling fundamentals:

* **Rule #1:** Derive you data model from queries - not the other way around.
* **Rule #2:** De-normalize data for fast access - no joins, no foreign keys!
* **Rule #3:** Forget about distributed (2-phase commit) transactions - instead use row level isolation, atomic batches and lightweight transactions.  

## Data Modeling in Action

### What we will build

Imagine that you are running a popular e-commerce platform - **AlleDeals**. The platform is going global and you must support the rapidly growing user base. You have a legacy relational, highly normalized database. The database is not scalable enough. 

Our goal is to port the relational data model to NoSQL Apache Cassandra, so it can **scale horizontally**. As a proof of concept, we will remodel the core domain entities: **Item** and **Purchase**.

### What we will use

We will use the following tools and frameworks:

* Gradle
* DataStax Java Driver 2.x
* Spring Data Cassandra
* JUnit
* AssertJ
* Achilles CQL embedded cassandra server

### How we will deliver

We will build and deliver the new data model in the following six short iterations. In each iteration, we will apply different Cassandra modeling techniques.

### Iteration #1 - User Registration (Static table)

The prerequisite in the selling/buying workflow is user registration. The user registration can be accomplished with a simple [Static table](http://planetcassandra.org/blog/datastax-developer-blog-a-thrift-to-cql3-upgrade-guide/).
A static table is very similar to relational table - each row generally has the same, static set of columns:

```sql
CREATE TABLE cass_user (
    user_id uuid,
    login varchar,
    age int,
    PRIMARY KEY (user_id)
);
```


The **primary key** of the User table is `user_id`. In Apache Cassandra, a primary key consists of two parts - mandatory [partitioning key](http://www.datastax.com/documentation/cql/3.1/cql/ddl/ddl_compound_keys_c.html) and optional [clustering key](http://www.datastax.com/documentation/cql/3.1/cql/ddl/ddl_compound_keys_c.html). The partitioning key determines the physical location of the row in the Apache Cassandra cluster. 
The clustering key specifies the sort order of columns within a row (partition) - clustering keys are used in Dynamic tables (wide rows) discussed in the next iterations. The User table defines only the partitioning key, which means that each user will be stored in a separate physical row.
The `user_id` is defined as the [uuid](http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/uuid_type_r.html) - this is the preferred type used in Apache Cassandra (there are no global sequences or auto-increment fields). 

In the Java code, the User table should be mapped to the following Spring Data Cassandra User class:

```java
@Table("cass_user")
public class User {

@PrimaryKey
private UUID userId;
private String login;
private int age;
    
}
```

The Spring Data Cassandra is relatively new in the [Spring Data](http://projects.spring.io/spring-data/) ecosystem. The Spring Data Cassandra version 1.0 GA is available since May 2014.

Thanks to its powerful entity mapping system and auto-generating query machanism, Spring Data dramatically speeds up data access / repository implementatiion. To map and query the user table in the Java code, you need to:
  
* create the User class with fields matching the user table
* annotate the User class with the `@Table` annotation
* annotate the User userId field with the `@PrimaryKey` annotation
* create the UserRepository interface extending from the `TypedIdCassandraRepository` repository

```java
public interface UserRepository extends TypedIdCassandraRepository<User, UUID> {
    
}
```
    

And that's all! You don't have to type a single line of code to save and get a User from Cassandra. Let's write an integration test to verify this.

In a test-driven development cycle, **integration tests should be fast and isolated** - they should be run within an in-memory database. For Apache Cassandra this can be achieved with [Achilles CQL embedded cassandra server](https://github.com/doanduyhai/Achilles/wiki/CQL-embedded-cassandra-server). The CQL embedded cassandra server is an **embedded database**, a counterpart of the embbedded SQL [H2 Database](http://www.h2database.com/html/main.html). The setup of the CQL embedded cassandra server is very straightforward, for details please refer to the [Achilles wiki](https://github.com/doanduyhai/Achilles/wiki/CQL-embedded-cassandra-server). 

Now, you can write your first Cassandra integration test:

```java
@RunWith(SpringJUnit4ClassRunner.class)
@CassandraIntegrationTest
public class UserRepositoryIntegrationTest {

    @Inject
    private UserRepository userRepository;

    @Test
    public void shouldInsertUser() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        String login = "cassandra_kata";
        int age = 34;
        User user = new User(uuid, login, age);

        //when
        userRepository.save(user);

        //then
        User foundUser = userRepository.findOne(uuid);
        assertThat(foundUser.getLogin()).isEqualTo(login);
        assertThat(foundUser.getAge()).isEqualTo(age);
    }
}
```

Now, run the test in your IDE and you should see a nice green bar :-) 

### Iteration #2 - Listing an Item (Static table)

As soon as a user is successfully registered in the system, the fun part begins - he or she can start selling and buying items :-)

The central entity is the **AlleDeals** core domain model is an **Item**. As you already learned, **Apache Cassandra data models are query-driven**. So let's consider the queries, which will be performed against the Item table:

* **Query #1:** Show item details (get item by id)
* **Query #2:** List user items (find items by user)
* **Query #3:** List tagged items (find items by tag)

In this iteration, we will implement the "Show item details" query. This will be the Item **Masterdata** table - the single-source-of-truth about the items. In the next two iterations, we will model two auxiliary tables for the items **Listing**. 

You may ask, why we need different model for item Listing? In CQL there is a **strong relationship between the table primary key definition and the queries, which can be performed against the table**. The only allowed columns in the CQL [WHERE](http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/select_r.html) clause are those defined in the table primary key or are indexed (secondary table index).

The primary key for the "Show item details" query should be the `item_id` - you will always request details for a specific item identified by its unique identifier. The primary key for the items Listing will depend on the given search criteria: either `user_id` or `tag name` respectively. This is the reason why we need separate tables - **each table will be specialized for a specific query**.

The definition of the Master Item table can be defined as follows:

```sql
CREATE TABLE cass_master_item (
    item_id timeuuid,
    user_id uuid,
    item_name varchar,
    item_desc text,
    unit_price decimal,
    offered_units int,
    available_units int,
    start_date timestamp,
    end_date timestamp,
    auction_finished boolean,
    PRIMARY KEY (item_id)
);
```
    
This is a Static table with a simple primary key composed only of the partitioning key. Please note the data type used for the `item_id`, it is defined as [timeuuid](http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/uuid_type_r.html). The timeuuid data type is extremely useful for [Time Series Data](http://planetcassandra.org/getting-started-with-time-series-data-modeling/), which you will soon implement for the items Listings.  
  
In the Java code, the Master Item table should be mapped to the following Spring Data Cassandra `Item` class:

```java
@Table("cass_master_item")
public class Item {

    @PrimaryKey
    private UUID itemId;

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

}
```
    
The corresponding Master Item repository should be implemented as follows:

```java
public interface ItemRepository extends TypedIdCassandraRepository<Item, UUID> {
    
}
```

And that's all - you can save and query items right away. Write an integration test to verify! 

### Iteration #3 - List Items by User (Dynamic table)

In the previous iteration we have built the Item Masterdata. The Item Masterdata can be queried only by a unique item identifier. The next step is to implement the first item Listing - **User Items**.

The User Items table should be defined as a [Dynamic table](http://planetcassandra.org/blog/datastax-developer-blog-a-thrift-to-cql3-upgrade-guide/). The [compound primary key](http://www.datastax.com/documentation/cql/3.1/cql/ddl/ddl_compound_keys_c.html) of the User Items table should consists of: `user_id` as the partitioning key
and `item_id` as the clustering key. The `item_id` should be defined as the [timeuuid](http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/uuid_type_r.html) data type. A value of timeuuid data type includes the time of its generation and are sorted by timestamp. The User Items should be sorted by the `item_id` ( incl. creation timestamp) in reverse order (latest item first on row):

```sql
CREATE TABLE cass_user_item (
    user_id uuid,
    item_id timeuuid,
    item_name varchar,
    unit_price decimal,
    available_units int,
    end_date timestamp,
    auction_finished boolean,
    PRIMARY KEY (user_id, item_id)
)
WITH CLUSTERING ORDER BY (item_id desc);
```
    
The **User Items** table should be a short [Time series](http://planetcassandra.org/getting-started-with-time-series-data-modeling/) wide row. To keep the row short, data (columns) in the User Items table should be stored with an [expiration date](http://www.datastax.com/documentation/cql/3.1/cql/cql_using/use_expire_c.html), e.g. 30 days. 

To store the whole **User Items History**, a separate table should be created. This is because **Cassandra stores an entire row of data on a node by partition key**. Assuming that there are users selling tens of thousands of items per month, the data should be spread over multiple nodes. 
Therefore, User Items History table should be defined with a [composite partitioning key](http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/refCompositePk.html), e.g. `user_id` combined with `year_month`.

In the Java code, the User Items **compound primary key** is represented by `UserItemKey`, a special [PrimaryKeyClass](http://docs.spring.io/spring-data/cassandra/docs/current/reference/htmlsingle/#cassandra-template.id-handling):

```java
@PrimaryKeyClass
public class UserItemKey implements Serializable {

    private static final long serialVersionUID = -791316285695123492L;

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "item_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID itemId;
    
}
```
    
Next, the User Items class should be defined as follows:

```java
@Table("cass_user_item")
public class UserItem {
    @PrimaryKey
    private UserItemKey id;
    
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
}
```
    
The corresponding User Item repository should be implemented as follows:

```java
public interface UserItemRepository extends TypedIdCassandraRepository<UserItem, UserItemKey> {
    
}
```
    
So far we have implemented the **Item Masterdata** and **User Items** tables, each serving its specific purpose. The final step is to keep both these tables in sync. This can be achieved with a Cassandra [Batch](http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/batch_r.html).

To implement a batch insert, you have to implement a [custom repository](http://docs.spring.io/spring-data/cassandra/docs/1.0.2.RELEASE/reference/html/repositories.html#repositories.custom-implementations) interface:

```java
public interface ItemRepositoryCustom {

    Item saveItem(Item item);
}
```
    
Next, add the custom repository to the main repository definition: 

```java
public interface ItemRepository extends TypedIdCassandraRepository<Item, UUID>, ItemRepositoryCustom {
    
}
```
    
Finally, implement the batch insert with the [DataStax QueryBuilder API](http://www.datastax.com/documentation/developer/java-driver/2.0/java-driver/reference/queryBuilderOverview.html):

```java
class ItemRepositoryImpl implements ItemRepositoryCustom {

    private CassandraOperations cassandraOperations;
    
    @Inject
    ItemRepositoryImpl(CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }
    
    @Override
    public Item saveItem(Item item) {
        Session session = cassandraOperations.getSession();
       
        PreparedStatement masterItem = session.prepare(masterItemInsert());
        PreparedStatement userItem = session.prepare(userItemInsertWithTtl());
        
        BatchStatement batch = new BatchStatement();
       
        //master item
        batch.add(masterItem.bind(item.getId(), item.getUserId(), item.getName(), item.getDescription(),
            item.getUnitPrice(), item.getOfferedUnits(), item.getStartDate(), item.getEndDate(), item.getTags()));
        //user item
        batch.add(userItem.bind(item.getUserId(), item.getId(), item.getName(), item.getUnitPrice(),
            item.getOfferedUnits(), item.getEndDate()));
                       
        cassandraOperations.execute(batch)
        
        return item;
    }
}
```

### Iteration #4 - List Items by Tag (Collections)

The second items Listing is built on the item metadata - **tags**. To list items by a tag, the data model must be enhanced with the following new features:

* **Item Masterdata** - new column to store item tags
* **Tag Item** - new table to store items by a tag

The first requirement can be fulfilled with the [Cassandra Collections](http://www.datastax.com/documentation/cql/3.0/cql/cql_using/use_collections_c.html). This is simple as adding a new column with one of the Cassandra's supported Collections types: **set**, **list**, **map**. Please note that Collections are designed to store only a small amount of data. The only elements supported by Collections are the simple types, e.g. int, text, uuid, etc. Currently, it is not possible to use nested Collections, i.e. you can not store a list as a map value, etc.

For the item tags, set is the most suitable Collection type:

```sql
CREATE TABLE cass_master_item (
    id timeuuid,
    user_id uuid,
    item_name varchar,
    item_desc text,
    unit_price decimal,
    offered_units int,
    available_units int,
    start_date timestamp,
    end_date timestamp,
    tags set<text>,
    auction_finished boolean,
    PRIMARY KEY (id)
);
```
    
In the Java code, the Cassandra set is mapped to **java.util.Set**:

```java 
@Table("cass_master_item")
public class Item {
 
    //other columns skipped here 

    @Column("tags")
        private Set<String> tags;
        
}
```
    
After Item Masterdata has been enhanced with tags, the second requirement can be implemented. Tagged items will be stored in a new Dynamic Table (wide row) - Tag Items: 

```sql
CREATE TABLE cass_tag_item (
    tag varchar,
    item_id timeuuid,
    item_name varchar,
    unit_price decimal,
    available_units int,
    end_date timestamp,
    auction_finished boolean,
    PRIMARY KEY (tag, item_id)
)
WITH CLUSTERING ORDER BY (item_id desc);
```
    
Please note that the **Tag Items** table has pretty much same structure as the **User Items** table. The only difference is the primary key definition, and more specifically the partitioning key (row key). Do you remember Iteration #2?  Let me recall this - **each table will be specialized for a specific query**. The **User Items** row will be looked up by the `user_id` and the **Tag Items** row will be looked up by `tag`. 

If you have strong background in the relational world, you will most likely disapprove this solution. Store redundant data just to perform different queries? Yes, in Apache Cassandra this is perfectly OK because of:

* Cassandra is optimized for writes, see the the [banchmarks](http://planetcassandra.org/nosql-performance-benchmarks/#netflix)
* Lookup by row key is fast and efficient
* Nowadays storage is cheap, execution time is the expensive factor - **online shoppers want speed! :-)**

The only remaining task to finish the **Tag Items** Listing, is to put everything in one [Batch](http://www.datastax.com/documentation/cql/3.1/cql/cql_reference/batch_r.html), so **Item Masterdata**, **User Items** and **Tag Items** are all kept in sync.

BatchStatement batch = new BatchStatement();

```java
//master item
batch.add(masterItem.bind(item.getId(), item.getUserId(), item.getName(), item.getDescription(),
    item.getUnitPrice(), item.getOfferedUnits(), item.getStartDate(), item.getEndDate(), item.getTags()));
//user item
batch.add(userItem.bind(item.getUserId(), item.getId(), item.getName(), item.getUnitPrice(),
    item.getOfferedUnits(), item.getEndDate()));
    
//tag items
for (String tag : item.getTags()) {
    batch.add(tagItem.bind(tag, item.getId(), item.getName(), item.getUnitPrice(), item.getOfferedUnits(), item.getEndDate()));
}
```

### Iteration #5 -  Purchasing an Item (Lightweight Transactions)

The final step in the listing/buying workflow is - **purchase**. A purchase is by definition a **transactional operation** - [ACID](http://en.wikipedia.org/wiki/ACID) to be more specific. In contrast, NoSQL databases are [BASE](http://en.wikipedia.org/wiki/Eventual_consistency) by design. 

And how does Apache Cassandra support transactions and concurrency? Below is a quote from the [Cassandra's documentation](http://www.datastax.com/documentation/cassandra/2.0/cassandra/dml/dml_about_transactions_c.html):

> **Cassandra does not use RDBMS ACID transactions with rollback or locking mechanisms**, but instead offers atomic, isolated, and durable transactions with eventual/tunable consistency that lets the user decide how strong or eventual they want each transaction’s consistency to be.
>
> As a non-relational database, **Cassandra does not support joins or foreign keys**, and consequently does not offer consistency in the ACID sense.
>
> **Cassandra supports atomicity and isolation at the row-level**, but trades transactional isolation and atomicity for high availability and fast write performance. **Cassandra writes are durable**.

The key concept in Apache Cassandra transactions and concurrency control is: **row-level atomicity and isolation (in this context row = partition)**. This means that **DML operations on rows sharing the same partition key for a table are performed atomically and in isolation**.

To take advantage of the Cassandra's transactional support, the purchase process should be split into the following steps:

* **Step 1:** purchase masterdata is saved with a short expiration date (e.g. 24h TTL). If the next step fails (including retries), the purchase will be automatically garbage-collected by Cassandra.
* **Step 2:** purchase is bound to item using [Lightweight Transactions](http://www.datastax.com/dev/blog/lightweight-transactions-in-cassandra-2-0) as an [optimistic lock](http://en.wikipedia.org/wiki/Optimistic_concurrency_control). The purchase-to-item link is applied on item row-level using [Collections](http://www.datastax.com/documentation/cql/3.1/cql/cql_using/use_collections_c.html), thus taking advantage of the Cassandra's row-level isolation. 
* **Step 3:** purchase bound to item triggers an event to complete the process (i.e. assign purchase to user account, update Listings, etc).

For **Step 1**, the following Static table can be used:

```sql
CREATE TABLE cass_master_purchase (
    id timeuuid,
    item_id timeuuid,
    user_id uuid,
    unit_price decimal,
    quantity int,
    status varchar,
    PRIMARY KEY (id)
);
```
    
After the "Buy now" button is clicked, a new purchase (status = Created) is saved with an **expiration date** set to 24h. Only after the purchase is successfully bound to an item, the expiration date is reset and the purchase is permanent. Otherwise, the purchase is automatically garbage-collected by Cassandra.

For **Step 2**, the Item Masterdata table has to be enhanced with a new Collection data type - the **purchases map**, where key is the `purchase_id` and value is the `quantity`:

```sql
CREATE TABLE cass_master_item (
    id timeuuid,
    user_id uuid,
    item_name varchar,
    item_desc text,
    unit_price decimal,
    offered_units int,
    available_units int,
    start_date timestamp,
    end_date timestamp,
    tags set<text>,
    purchases map<timeuuid, int>, => Takes advantage of row level isolation
    auction_finished boolean,
    PRIMARY KEY (id)
);
```
    
For popular items, the challange is to properly handle the [race conditions](http://en.wikipedia.org/wiki/Race_condition). This can be acomplished with the Cassandra's Lightweight Transactions - **UPDATE..IF**:

```java
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
```
            
In this example, the **optimistic lock** is applied on the **available units** column. If the value (snapshot) has changed between the purchase is created and bound to an item, the operation is rejected. If the item is still available, the operation should be retried.

For **Step 3**, after a purchase is linked with an item and the item availability is updated, **an event should be triggered to complete the purchase**. 

In the example code, Google Guava [EventBus](https://code.google.com/p/guava-libraries/wiki/EventBusExplained) is used. In a production system, the event should be published to a [reliable messaging system](http://en.wikipedia.org/wiki/Reliable_messaging) with **guaranteed message delivery**.

```java
Purchase purchase = new Purchase(UUIDs.timeBased(), item.getId(), user.getId(), item.getUnitPrice(), quantity);
purchaseRepository.savePurchase(purchase, TTL_24H);

if (purchaseRepository.bindPurchaseToItem(purchase, item)) {
    eventPublisher.publish(new PurchaseBoundEvent(purchase));
}
```
    
And that's all for this iteration. We will handle the **PurchaseBoundEvent** in the next iteration, so stay tuned! :-) 

### Iteration #6 - Complete a Purchase

After a purchase is placed, the following **background tasks** must be perfomed (this is a very simplistic example):

* **Task 1:** purchase masterdata expiration date is reset; purchase is made permanent
* **Task 2:** purchase is assigned to user account
* **Task 3:** item listings are updated

To keep the purchase data consistent, **Task 1** and **Task 2** will be combined into one Batch operation. The purchase update operation should be triggered by the **PurchaseBoundEvent**:

```java 
@Component
public class PurchaseEventSubscriber {
    
    private PurchaseService purchaseService;
    
    @Subscribe
    public void onEvent(PurchaseBoundEvent event) {
        Purchase purchase = event.getPurchase();

        purchaseService.completePurchase(purchase);
    }
}
```
    
And, the Batch update should be carried out in the repository:

```java
BatchStatement batch = new BatchStatement();

batch.add(purchaseStatusUpdate.bind(Purchase.STATUS_COMPLETED, purchase.getId()));
batch.add(userPurchaseInsert.bind(purchase.getUserId(), purchase.getId(), purchase.getItemId(), purchase.getUnitPrice(), purchase.getQuantity()));
```
    
To keep the item Listings up to date with the Item Masterdata, there must be a separate event subscriber for item changes:

```java
@Component
public class ItemEventSubscriber {
    
    private ItemService itemService;
        
    @Subscribe
    public void onEvent(PurchaseBoundEvent event) {
        Purchase purchase = event.getPurchase();

        itemService.updateListings(purchase.getItemId());
    }
}
```
       
And the corresponding repository:

```java
public void updateListingItems(UUID itemId) {
    Item item = itemRepository.findOne(itemId);

    updateUserItem(item);
    updateTagItems(item);
}
```
        
The exercise is complete! The purchase is placed in the system, item masterdata and the corresponding Listings are updated. The whole process (though very simplified) has been accomplished. Well done! :-)

## Conclusion

In this kata, you have learned that Apache Cassandra is a great, powerful NoSQL storage system with a very flexible modeling capabilities. Although, this is not a [Silver Bullet](http://en.wikipedia.org/wiki/No_Silver_Bullet) - there are other specialized NoSQL storage systems (e.g. document, graph oriented) and of course the good old relational databases. Nowadays, we operate in the [Polyglot Persistence](http://martinfowler.com/bliki/PolyglotPersistence.html) environment. Therefore, remember - **use the right tool for the right job!** :-)