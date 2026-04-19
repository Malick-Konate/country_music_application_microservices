-- 1. INSERT ORDERS
INSERT INTO orders (order_id, user_id, order_status)
VALUES ('ord_101', 'user-001', 'COMPLETED'),
       ('ord_102', 'user-002', 'COMPLETED'),
       ('ord_103', 'user-003', 'PENDING'),
       ('ord_104', 'user-004', 'COMPLETED'),
       ('ord_105', 'user-005', 'CANCELLED'),
       ('ord_106', 'user-006', 'COMPLETED'),
       ('ord_107', 'user-007', 'PENDING'),
       ('ord_108', 'user-005', 'COMPLETED'),
       ('ord_109', 'user-001', 'COMPLETED'),
       ('ord_110', 'user-006', 'COMPLETED');

-- 2. INSERT ORDER ITEMS
INSERT INTO order_items (order_id, product_type, display_name, price, quantity, artist_name)
VALUES
    ('ord_101', 'ALBUM_PURCHASE', 'Whiskey Roads', 14.99, 1, 'Johnny Cash'),
    ('ord_102', 'PODCAST_SUBSCRIPTION', 'Nashville Insider', 4.99, 1, 'Sarah Twang'),
    ('ord_103', 'ARTIST_DONATION', 'Artist Support', 25.00, 1, 'Johnny Cash'),
    ('ord_104', 'ALBUM_PURCHASE', 'Cowboy Sunset', 14.99, 1, 'Reba McEntire'),
    ('ord_104', 'ARTIST_DONATION', 'Tip Jar', 5.00, 1, 'Reba McEntire'),
    ('ord_105', 'ALBUM_PURCHASE', 'Southern Skies', 12.99, 1, 'Dolly Parton'),
    ('ord_106', 'PODCAST_SUBSCRIPTION', 'Honky Tonk History', 4.99, 1, 'Clint Blackman'),
    ('ord_107', 'ALBUM_PURCHASE', 'Heartland Echoes', 14.99, 1, 'Willie Nelson'),
    ('ord_108', 'ARTIST_DONATION', 'Monthly Patron', 10.00, 1, 'Chris Stapleton'),
    ('ord_109', 'ALBUM_PURCHASE', 'Gravel and Grace', 14.99, 1, 'Shania Twain'),
    ('ord_110', 'PODCAST_SUBSCRIPTION', 'Bluegrass Breakdown', 0.00, 1, 'Earl Scruggs Jr.');

-- 3. INSERT PAYMENTS
INSERT INTO payment (order_id, amount, paid_at, method, payment_status, currency)
VALUES ('ord_101', 14.99, '2023-10-01 10:00:00', 'CREDIT_CARD', 'COMPLETED', 'USD'),
       ('ord_102', 4.99, '2023-10-02 11:30:00', 'PAYPAL', 'COMPLETED', 'USD'),
       ('ord_103', 25.00, NULL, 'STRIPE', 'PENDING', 'USD'),
       ('ord_104', 19.99, '2023-10-04 09:15:00', 'CREDIT_CARD', 'COMPLETED', 'USD'),
       ('ord_105', 12.99, NULL, 'CREDIT_CARD', 'FAILED', 'USD'),
       ('ord_106', 4.99, '2023-10-06 14:00:00', 'PAYPAL', 'COMPLETED', 'USD'),
       ('ord_107', 14.99, NULL, 'STRIPE', 'PENDING', 'USD'),
       ('ord_108', 10.00, '2023-10-08 16:45:00', 'CREDIT_CARD', 'COMPLETED', 'USD'),
       ('ord_109', 14.99, '2023-10-09 12:00:00', 'STRIPE', 'COMPLETED', 'USD'),
       ('ord_110', 0.00, '2023-10-10 08:00:00', 'CREDIT_CARD', 'COMPLETED', 'USD');