-- Use backticks because 'order' is a reserved keyword in SQL
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders`
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    order_id     VARCHAR(100)                               NOT NULL UNIQUE, -- From OrderIdentifier
    user_id      VARCHAR(100)                               NOT NULL,        -- From UserIdentifier
    order_status ENUM ('PENDING', 'COMPLETED', 'CANCELLED') NOT NULL
);

CREATE TABLE order_items
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    order_id     VARCHAR(100)                                                       NOT NULL,
    product_type ENUM ('ALBUM_PURCHASE', 'PODCAST_SUBSCRIPTION', 'ARTIST_DONATION') NOT NULL,
    display_name VARCHAR(255),
    price        DECIMAL(19, 2),
    quantity     INT,
    artist_name  VARCHAR(255)
);

CREATE TABLE payment
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    order_id       VARCHAR(100) NOT NULL,
    amount         DECIMAL(19, 2),
    paid_at        DATETIME,
    method         ENUM ('CREDIT_CARD', 'PAYPAL', 'STRIPE'),
    payment_status ENUM ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'),
    currency       VARCHAR(10)
);
