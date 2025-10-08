CREATE TABLE IF NOT EXISTS Persona (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
    );

INSERT INTO Persona (name, email) VALUES
                                      ('alice', 'user@user.co'),
                                      ('bob', 'user2@user.com');

CREATE TABLE IF NOT EXISTS user_bootcamp (
                                             user_id BIGINT NOT NULL,
                                             bootcamp_id BIGINT NOT NULL,
                                             primary key (user_id, bootcamp_id)
);
