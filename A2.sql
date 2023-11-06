CREATE DATABASE IF NOT EXISTS albumDB;
USE albumDB;

CREATE TABLE albums (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    year VARCHAR(4),
    image LONGBLOB,
);