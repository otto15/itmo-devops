CREATE TABLE cars
(
    id            BIGSERIAL    PRIMARY KEY,
    brand         VARCHAR(100) NOT NULL,
    model         VARCHAR(100) NOT NULL,
    year          INTEGER      NOT NULL,
    color         VARCHAR(50)  NOT NULL,
    license_plate VARCHAR(20)  NOT NULL UNIQUE
);
