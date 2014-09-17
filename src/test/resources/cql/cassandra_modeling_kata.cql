CREATE TABLE cass_user (
    id uuid,
    login varchar,
    age int,
    PRIMARY KEY (id)
);

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
    purchases map<uuid, int>,
    auction_finished boolean,
    PRIMARY KEY (id)
);

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

CREATE TABLE cass_master_purchase (
    id timeuuid,
    item_id timeuuid,
    user_id uuid,
    unit_price decimal,
    quantity int,
    status varchar,
    PRIMARY KEY (id)
);

CREATE TABLE cass_user_purchase (
    user_id uuid,
    purchase_id timeuuid,
    item_id timeuuid,
    unit_price decimal,
    quantity int,
    PRIMARY KEY (user_id, purchase_id)
)
WITH CLUSTERING ORDER BY (purchase_id desc);


CREATE INDEX idx_master_item_finished ON cass_master_item (auction_finished);
