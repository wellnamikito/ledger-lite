CREATE DOMAIN email_domain AS VARCHAR(255)
    CHECK ( VALUE ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' );

CREATE DOMAIN fio_domain AS VARCHAR(100)
    CHECK ( VALUE ~ '^[A-Za-zА-Яа-яЁё\- ]+$' );

CREATE TABLE IF NOT EXISTS owners(
    id UUID PRIMARY KEY,
    last_name fio_domain NOT NULL,
    first_name fio_domain NOT NULL,
    middle_name fio_domain,
    email email_domain NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT  now()
);