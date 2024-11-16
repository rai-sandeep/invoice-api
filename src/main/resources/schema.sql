CREATE TABLE invoice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount DECIMAL(19, 2) NOT NULL,
    paid_amount DECIMAL(19, 2),
    due_date DATE,
    status VARCHAR(255)
);

ALTER TABLE invoice ALTER COLUMN id RESTART WITH 1234;