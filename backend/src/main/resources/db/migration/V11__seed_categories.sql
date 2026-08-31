INSERT INTO categories (id, name, type) VALUES
    (gen_random_uuid(), 'Gehalt', 'INCOME'),
    (gen_random_uuid(), 'Sonstige Einnahmen', 'INCOME'),
    (gen_random_uuid(), 'Wohnen', 'EXPENSE'),
    (gen_random_uuid(), 'Lebensmittel', 'EXPENSE'),
    (gen_random_uuid(), 'Transport', 'EXPENSE'),
    (gen_random_uuid(), 'Freizeit', 'EXPENSE'),
    (gen_random_uuid(), 'Versicherung', 'EXPENSE'),
    (gen_random_uuid(), 'Sonstiges', 'EXPENSE');
