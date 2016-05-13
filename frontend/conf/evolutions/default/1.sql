# --- !Ups

CREATE TABLE `parameters` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `value` text NOT NULL,
  `clazz` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

# --- !Downs

DROP TABLE `parameters`;
