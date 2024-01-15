DROP SEQUENCE IF EXISTS user_seq;
DROP SEQUENCE IF EXISTS image_data_seq;
DROP SEQUENCE IF EXISTS question_seq;
DROP SEQUENCE IF EXISTS question_rating_seq;
DROP SEQUENCE IF EXISTS answer_seq;
DROP SEQUENCE IF EXISTS answer_rating_seq;

DROP TABLE IF EXISTS answer_rating;
DROP TABLE IF EXISTS answer;
DROP TABLE IF EXISTS question_rating;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS image_data;
DROP TABLE IF EXISTS _user;

CREATE TABLE _user (
    id serial PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(68) NOT NULL,
    active BOOLEAN NOT NULL,
    verification_code VARCHAR(64),
    email VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    refresh_token VARCHAR,
    access_token VARCHAR,
    img VARCHAR UNIQUE,
    forgot_password_code VARCHAR(64)
);

CREATE TABLE image_data (
    id serial PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    file_path VARCHAR NOT NULL UNIQUE,
    user_id INT,
    CONSTRAINT fk_image_data_user FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE TABLE question (
    id serial PRIMARY KEY,
    title VARCHAR(245) NOT NULL,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    post_timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES _user (id)
);

CREATE TABLE question_rating (
    id serial PRIMARY KEY,
    upvote BOOLEAN NOT NULL,
    downvote BOOLEAN NOT NULL,
    user_id INT NOT NULL,
    question_id INT NOT NULL,
    UNIQUE (user_id, question_id),
    CONSTRAINT fk_question_rating_user FOREIGN KEY (user_id) REFERENCES _user(id),
    CONSTRAINT fk_question_rating_question FOREIGN KEY (question_id) REFERENCES question(id)
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

CREATE TABLE answer_rating (
    id serial PRIMARY KEY,
    upvote BOOLEAN NOT NULL,
    downvote BOOLEAN NOT NULL,
    answer_id INT NOT NULL,
    user_id INT NOT NULL,
    UNIQUE (answer_id, user_id),
    CONSTRAINT fk_answer_rating_answer FOREIGN KEY (answer_id) REFERENCES answer(id),
    CONSTRAINT fk_answer_rating_user FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE SEQUENCE user_seq INCREMENT 1 START 2;
CREATE SEQUENCE image_data_seq INCREMENT 1 START 2;
CREATE SEQUENCE question_seq INCREMENT 1 START 3;
CREATE SEQUENCE question_rating_seq INCREMENT 1 START 2;
CREATE SEQUENCE answer_seq INCREMENT 1 START 3;
CREATE SEQUENCE answer_rating_seq INCREMENT 1 START 2;