--!Ups
CREATE TABLE `Directors`(
  `id` INTEGER PRIMARY KEY,
  `name` VARCHAR NOT NULL
);

CREATE TABLE `Actors`(
  `id` INTEGER PRIMARY KEY,
  `name` VARCHAR NOT NULL
);

CREATE TABLE `Genres`(
  `id` INTEGER PRIMARY KEY,
  `name` VARCHAR NOT NULL
);

CREATE TABLE `Users`(
  `id` INTEGER PRIMARY KEY,
  `email` VARCHAR NOT NULL,
  `password`  VARCHAR NOT NULL,
  `surname` VARCHAR NOT NULL,
  `name` VARCHAR NOT NULL,
  `address` VARCHAR NOT NULL,
  `zipcode` VARCHAR NOT NULL,
  `city` VARCHAR NOT NULL,
  `country` VARCHAR NOT NULL
);

CREATE TABLE `Films`(
  `id` INTEGER PRIMARY KEY,
  `name` VARCHAR NOT NULL,
  `director_id` INTEGER NOT NULL,
  `genre_id` INTEGER NOT NULL,
  `actor_id` INTEGER NOT NULL,
  FOREIGN KEY (`director_id`)
    REFERENCES `Directors` (`id`),
  FOREIGN KEY (`genre_id`)
    REFERENCES `Genres` (`id`),
  FOREIGN KEY (`actor_id`)
    REFERENCES `Actors` (`id`)
);

CREATE TABLE `Favourites`(
  `id` INTEGER PRIMARY KEY,
  `film_id` INTEGER NOT NULL,
  FOREIGN KEY (`film_id`)
    REFERENCES `Films` (`id`)
);

CREATE TABLE `Payments`(
  `id` INTEGER PRIMARY KEY,
  `user_id` INTEGER NOT NULL,
  `amount` INTEGER NOT NULL,
   FOREIGN KEY (`user_id`)
    REFERENCES `Users` (`id`)
);

CREATE TABLE `Orders`(
  `id` INTEGER PRIMARY KEY,
  `user_id` INTEGER NOT NULL,
  `payment_id` INTEGER NOT NULL,
  FOREIGN KEY (`user_id`)
    REFERENCES `Users` (`id`),
  FOREIGN KEY (`payment_id`)
    REFERENCES `Payments` (`id`)
);


CREATE TABLE `ShoppingBags`(
  `id` INTEGER PRIMARY KEY,
  `total_cost` INTEGER,
  `film_id` INTEGER,
   FOREIGN KEY (`film_id`)
    REFERENCES `Films` (`id`)  
);


CREATE TABLE `Reviews`(
  `id` INTEGER PRIMARY KEY,
  `stars` INTEGER NOT NULL,
  `txt` VARCHAR,
  `user_id` INTEGER NOT NULL,
   FOREIGN KEY (`user_id`)
    REFERENCES `Users` (`id`)  
);

--!Downs

DROP TABLE IF EXISTS `Directors`;
DROP TABLE IF EXISTS `Actors`;
DROP TABLE IF EXISTS `Genres`;
DROP TABLE IF EXISTS `Users`;
DROP TABLE IF EXISTS `Films`;
DROP TABLE IF EXISTS `Favourites`;
DROP TABLE IF EXISTS `Payments`;
DROP TABLE IF EXISTS `Orders`;
DROP TABLE IF EXISTS `ShoppingBag`;
DROP TABLE IF EXISTS `Reviews`;
