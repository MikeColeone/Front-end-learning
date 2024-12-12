CREATE TABLE `users` (
     id INT AUTO_INCREMENT PRIMARY KEY,
     username VARCHAR(255) NOT NULL,
     phone CHAR(11) NOT NULL,
     account CHAR(10) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     role ENUM('admin', 'teacher','superAdmin') NOT NULL,
     insert_time    datetime    not null default current_timestamp,
     update_time    datetime    not null default current_timestamp on update current_timestamp

);

CREATE TABLE `labs` (
    id TINYINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    appointment JSON NULL COMMENT '{"week":[0],"isSelected"}',
    totals INT NOT NULL,
    information JSON null comment '{"system":{"name","version"}}',
    news JSON null comment '{"description","isSelect"}',
    name VARCHAR(255) NOT NULL,
    insert_time    datetime    not null default current_timestamp,
    update_time    datetime    not null default current_timestamp on update current_timestamp
);

CREATE TABLE `courses` (
   id INT AUTO_INCREMENT PRIMARY KEY,
   uid INT NOT NULL,
   lid INT NOT NULL,
   name VARCHAR(255) NOT NULL,
   information json null comment '{"department","grade","class"}',
   week JSON,
   time VARCHAR(255),
   insert_time    datetime    not null default current_timestamp,
   update_time    datetime    not null default current_timestamp on update current_timestamp
);
