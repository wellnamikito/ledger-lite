CREATE TABLE IF NOT EXISTS account_types(
    id SMALLINT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

INSERT INTO account_types (id, code, description) VALUES
(1, 'CHECKING', 'Обычный расчётный счёт'),
(2, 'SAVINGS', 'Сберегательный счёт / вклад');