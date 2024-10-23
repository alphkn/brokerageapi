INSERT INTO customer (is_enabled)
VALUES (TRUE);

INSERT INTO "user" (is_account_non_expired, is_account_non_locked, is_credentials_non_expired,
                    is_enabled, password, username, customer_id)
VALUES (TRUE, TRUE, TRUE, TRUE, '$2a$10$VyiaWO8HENuRfy71svprheSVVbLUf.Q4MYLPjZ0emfG9PNnVsP1gW',
        'system', 1);

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ADMIN');

UPDATE customer
SET user_id = 1
WHERE id = 1;

INSERT INTO asset (asset_code, size, usable_size, customer_id)
VALUES ('TRY', 0.00, 0.00, 1);

INSERT INTO asset (asset_code, size, usable_size, customer_id)
VALUES ('GARAN', 10000.00, 0.00, 1);

INSERT INTO asset (asset_code, size, usable_size, customer_id)
VALUES ('ING', 10000.00, 0.00, 1);

INSERT INTO asset (asset_code, size, usable_size, customer_id)
VALUES ('SASA', 10000.00, 0.00, 1);

INSERT INTO trade_order (asset_code, create_date, order_side, price, size, status, customer_id)
VALUES ('GARAN', '2024-10-23 17:04:52.747765', 'SELL', 20.00, 10000.00, 'PENDING', 1);

INSERT INTO trade_order (asset_code, create_date, order_side, price, size, status, customer_id)
VALUES ('ING', '2024-10-23 17:04:52.747765', 'SELL', 20.00, 10000.00, 'PENDING', 1);

INSERT INTO trade_order (asset_code, create_date, order_side, price, size, status, customer_id)
VALUES ('SASA', '2024-10-23 17:04:52.747765', 'SELL', 20.00, 10000.00, 'PENDING', 1);

