
/*
sau daca e creata deja structura:

ALTER TABLE `android`.`history` 
ADD UNIQUE `user_book_UNIQUE` (`history_user_id`,`history_book_id`) ;
*/

CREATE TABLE `authors` (
  `author_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`author_id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `books` (
  `book_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) CHARACTER SET utf8 NOT NULL,
  `book_author_id` int(11) NOT NULL,
  `publisher` varchar(50) CHARACTER SET utf8 NOT NULL,
  `description` text CHARACTER SET utf8 NOT NULL,
  `total` int(11) unsigned NOT NULL,
  `available` int(11) unsigned NOT NULL,
  `cover` blob,
  PRIMARY KEY (`book_id`),
  UNIQUE KEY `title_publisher_UNIQUE` (`title`,`book_author_id`,`publisher`),
  KEY `Books_fk0` (`book_author_id`),
  CONSTRAINT `Books_fk0` FOREIGN KEY (`book_author_id`) REFERENCES `authors` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_romanian_ci;


CREATE TABLE `favourites` (
  `fav_id` int(11) NOT NULL AUTO_INCREMENT,
  `fav_user_id` int(11) NOT NULL,
  `fav_book_id` int(11) NOT NULL,
  PRIMARY KEY (`fav_id`),
  UNIQUE KEY `user_book_UNIQUE` (`fav_user_id`,`fav_book_id`),
  KEY `Favourites_fk1` (`fav_book_id`),
  CONSTRAINT `Favourites_fk0` FOREIGN KEY (`fav_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `Favourites_fk1` FOREIGN KEY (`fav_book_id`) REFERENCES `books` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `history` (
  `history_id` int(11) NOT NULL AUTO_INCREMENT,
  `history_user_id` int(11) NOT NULL,
  `history_book_id` int(11) NOT NULL,
  `loan_date` datetime NOT NULL,
  `return_date` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  UNIQUE KEY `user_book_UNIQUE` (`history_user_id`,`history_book_id`),
  KEY `History_fk0` (`history_user_id`),
  KEY `History_fk1` (`history_book_id`),
  CONSTRAINT `History_fk0` FOREIGN KEY (`history_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `History_fk1` FOREIGN KEY (`history_book_id`) REFERENCES `books` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `fullname` varchar(100) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
