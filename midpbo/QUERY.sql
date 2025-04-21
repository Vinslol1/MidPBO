CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    fullname TEXT NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL,
    name TEXT NOT NULL,
    price FLOAT NOT NULL,
    stock INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS perishable_products(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expired_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS bundle_products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bundleName TEXT NOT NULL,
    bundlePrice REAL,
    bundleDiscount INTEGER,
    bundleDescription TEXT
);

CREATE TABLE IF NOT EXISTS bundle_product_items (
    bundle_id INTEGER,
    product_id INTEGER,
    FOREIGN KEY(bundle_id) REFERENCES bundle_products(id),
    FOREIGN KEY(product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id TEXT PRIMARY KEY,
    date TEXT NOT NULL,
    type TEXT NOT NULL,
    total REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS transaction_items (
    transaction_id TEXT,
    product_code TEXT,
    quantity INTEGER,
    price_per_unit REAL,
    FOREIGN KEY(transaction_id) REFERENCES transactions(id)
);
