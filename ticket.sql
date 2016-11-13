DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `route`;
# 车号，路线
CREATE TABLE `route` (
  `trainname` VARCHAR(255) PRIMARY KEY NOT NULL,
  `way` VARCHAR(255) NOT NULL,
  `size` int(11)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
LOAD DATA LOCAL INFILE "/Users/phoebegl/ticket/routes.txt" INTO TABLE route CHARACTER SET 'utf8' fields terminated by ' ' lines terminated by '\r\n';
update route set size = 8 limit 100 ;
update route set size = 16 WHERE size IS NULL ;

DROP TABLE IF EXISTS `remain`;
CREATE TABLE `remain` (
  #车号,车站,日期,剩余数,席别
  `trainname` VARCHAR(255) NOT NULL,
  `station` VARCHAR(255) NOT NULL,
  `time` DATE NOT NULL ,
  `remainnum` int(11) NOT NULL ,
  `type` VARCHAR(255) NOT NULL ,
  `version` INT(11) DEFAULT 1,
  PRIMARY KEY (`trainname`,`station`,`time`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `remaining`;
CREATE TABLE `remaining` (
#车号,起点,终点,日期,剩余数,席别
  `trainname` VARCHAR(255) NOT NULL,
  `start` VARCHAR(255) NOT NULL,
  `end` VARCHAR(255) NOT NULL,
  `time` DATE NOT NULL ,
  `remainnum` int(11) NOT NULL ,
  `type` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`trainname`,`start`,`end`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `carriage`;
CREATE TABLE `carriage` (
  #车号,车厢号,位置,种类,行数,列数,基价
  `id` VARCHAR(255) PRIMARY KEY NOT NULL,
  `trainname` VARCHAR(255) NOT NULL,
  `location` int(11) NOT NULL,
  `type` VARCHAR(255) NOT NULL,
  `row` int(11) NOT NULL ,
  `column` int(11) NOT NULL,
  `price` int(11) DEFAULT 0 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `occupy`;
CREATE TABLE `occupy` (
  #id,车厢号,行号,列号,起点,终点,日期
  `id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `carriageid` VARCHAR(255) NOT NULL ,
  `row` int(11) NOT NULL ,
  `column` int(11) NOT NULL ,
  `start` VARCHAR(255) NOT NULL,
  `end` VARCHAR(255) NOT NULL,
  `time` DATE NOT NULL,
  FOREIGN KEY (`carriageid`) REFERENCES `carriage`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  #id,用户名,车号,occupyid
  `id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `trainname` VARCHAR(255) NOT NULL,
  `occupyid` int(11) NOT NULL ,
  FOREIGN KEY (`occupyid`) REFERENCES `occupy`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

