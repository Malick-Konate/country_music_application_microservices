-- Drop tables if they exist
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;

-- 1. Create Orders Table
CREATE TABLE orders
(
    id           SERIAL PRIMARY KEY,
    order_id     VARCHAR(100) NOT NULL UNIQUE,
    user_id      VARCHAR(100) NOT NULL,
    -- User already converted this to VARCHAR + CHECK
    order_status VARCHAR(20)  NOT NULL CHECK (order_status IN ('PENDING', 'COMPLETED', 'CANCELLED'))
);

-- 2. Create Order Items Table
CREATE TABLE order_items
(
    id           SERIAL PRIMARY KEY,
    order_id     VARCHAR(100) NOT NULL REFERENCES orders (order_id),
    -- Replaced product_type_enum with VARCHAR + CHECK
    product_type VARCHAR(30)  NOT NULL CHECK (product_type IN ('ALBUM_PURCHASE', 'PODCAST_SUBSCRIPTION', 'ARTIST_DONATION')),
    display_name VARCHAR(255),
    price        DECIMAL(19, 2),
    quantity     INT,
    artist_name  VARCHAR(255)
);

-- 3. Create Payment Table
CREATE TABLE payment
(
    id             SERIAL PRIMARY KEY,
    order_id       VARCHAR(100) NOT NULL REFERENCES orders (order_id),
    amount         DECIMAL(19, 2),
    paid_at        TIMESTAMP,
    -- Replaced payment_method_enum with VARCHAR + CHECK
    method         VARCHAR(20)  CHECK (method IN ('CREDIT_CARD', 'PAYPAL', 'STRIPE')),
    -- Replaced payment_status_enum with VARCHAR + CHECK
    payment_status VARCHAR(20)  CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    currency       VARCHAR(10)
);