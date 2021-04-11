CREATE SCHEMA `demospring` ;
CREATE TABLE `demospring`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `address` VARCHAR(500) NOT NULL,
  `password` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE);


Insert into `demoSpring`.user(name,email,address,password) values ("Monica","monicagellar@gmail.com","New York","abc123");
Insert into `demoSpring`.user(name,email,address,password) values ("Ross","rossgellar@gmail.com","New York","abc123");
Insert into `demoSpring`.user(name,email,address,password) values ("Chandler","chandlerbing@gmail.com","New York","abc123");
Insert into `demoSpring`.user(name,email,address,password) values ("Joey","joeytribbiani@gmail.com","New York","abc123");
Insert into `demoSpring`.user(name,email,address,password) values ("Phoebe","phoebebuffay@gmail.com","New York","abc123");
Insert into `demoSpring`.user(name,email,address,password) values ("Rachel","rachelgreen@gmail.com","New York","abc123");