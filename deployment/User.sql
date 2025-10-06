CREATE TABLE IF NOT EXISTS "User" (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
);

INSERT INTO "User" (name, email) VALUES
('alice', 'user@user.co',
('bob', 'user2@user.com');

CREATE TABLE IF NOT EXISTS user_bootcamp (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bootcamp_id BIGINT NOT NULL,
    registered_at DATE
);