CREATE TABLE `users` (
     id INT PRIMARY KEY,
     username VARCHAR(255) NOT NULL,
     phone CHAR(11) NOT NULL,
     account CHAR(10) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     role ENUM('admin', 'teacher', 'superAdmin') NOT NULL,
     insert_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     UNIQUE (username),
     UNIQUE (phone),
     UNIQUE (account)
);

CREATE TABLE `labs` (
    id TINYINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    totals INT NOT NULL,
    information JSON NULL,
    news JSON NULL,
    name VARCHAR(255) NOT NULL,
    insert_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX (number),
    INDEX (name)
);

CREATE TABLE `courses` (
   id INT AUTO_INCREMENT PRIMARY KEY,
   uid INT NOT NULL,
   lid INT NOT NULL,
   name VARCHAR(255) NOT NULL,
   information JSON NULL,
   week JSON,
   time VARCHAR(255),
   insert_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (uid) REFERENCES `users`(id),
   FOREIGN KEY (lid) REFERENCES `labs`(id)
);

CREATE TABLE `appointment` (
   id INT AUTO_INCREMENT PRIMARY KEY,
   uid INT NOT NULL,
   lid INT NOT NULL,
   cid INT NOT NULL,
   status ENUM('pending', 'approved', 'rejected', 'canceled') NOT NULL DEFAULT 'pending',  # 待处理 通过 拒绝 取消
   details JSON NULL COMMENT '{
        "reason": "预约原因",
        "duration": {"start": "2024-07-01 10:00", "end": "2024-07-01 12:00"},
        "participants": ["张三", "李四", "王五"]
    }',
   insert_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (`uid`) REFERENCES `users`(`id`),
   FOREIGN KEY (`lid`) REFERENCES `labs`(`id`),
   FOREIGN KEY (`cid`) REFERENCES `courses`(`id`)
);

