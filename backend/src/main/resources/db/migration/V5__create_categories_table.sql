CREATE TABLE categories (
    id   UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT uq_category_name_type UNIQUE (name, type)
);
