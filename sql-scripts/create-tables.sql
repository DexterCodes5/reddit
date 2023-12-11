DROP SEQUENCE IF EXISTS user_seq;
DROP SEQUENCE IF EXISTS image_data_seq;
DROP SEQUENCE IF EXISTS question_seq;
DROP SEQUENCE IF EXISTS answer_seq;

DROP TABLE IF EXISTS answer;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS image_data;
DROP TABLE IF EXISTS _user;

CREATE TABLE _user (
    id serial PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(68) NOT NULL,
    active BOOLEAN NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    refresh_token VARCHAR,
    img VARCHAR UNIQUE
);

CREATE TABLE image_data (
    id serial PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    file_path VARCHAR NOT NULL UNIQUE
);

CREATE TABLE question (
    id serial PRIMARY KEY,
    title VARCHAR(245) NOT NULL,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    post_timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES _user (id)
);

CREATE TABLE answer (
    id serial PRIMARY KEY,
    question_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    post_timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_question FOREIGN KEY (question_id) REFERENCES question(id),
    CONSTRAINT fk_answer_user FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE SEQUENCE user_seq INCREMENT 1 START 1;
CREATE SEQUENCE image_data_seq INCREMENT 1 START 1;
CREATE SEQUENCE question_seq INCREMENT 1 START 1;
CREATE SEQUENCE answer_seq INCREMENT 1 START 1;