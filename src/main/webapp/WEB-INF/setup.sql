CREATE TABLE IF NOT EXISTS auctions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_name TEXT NOT NULL,
    start_price INTEGER NOT NULL,
    auction_duration INTEGER NOT NULL,
    reserve_price INTEGER,
    decrement_amount INTEGER,
    decrement_interval_mins INTEGER,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    auction_type TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS bids (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    auction_id INTEGER,
    bidder_id TEXT NOT NULL,
    bid_amount INTEGER NOT NULL,
    bid_time TEXT NOT NULL,
    FOREIGN KEY (auction_id) REFERENCES auctions(id)
);
