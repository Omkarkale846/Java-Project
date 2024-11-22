CREATE DATABASE FitnessTracker;

USE FitnessTracker;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    age INT
);

CREATE TABLE workouts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    workout VARCHAR(255),
    duration INT,
    date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE bmi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    height DOUBLE,
    weight DOUBLE,
    bmi_value DOUBLE,
    date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
