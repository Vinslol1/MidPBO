CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    fullname TEXT NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    stock INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS bundle_product (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bundleName TEXT NOT NULL,
    bundlePrice REAL,
    bundleDiscount INTEGER,
    bundleDescription TEXT
);

CREATE TABLE IF NOT EXISTS bundle_product_items (
    bundle_id INTEGER,
    product_id INTEGER
    FOREIGN KEY(bundle_id) REFERENCES bundle_product(id),
    FOREIGN KEY(product_id) REFERENCES product(id),
);
