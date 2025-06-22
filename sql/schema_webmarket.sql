DROP DATABASE IF EXISTS webmarket;
CREATE DATABASE webmarket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE webmarket;

-- Users
CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin', 'technician', 'purchaser') NOT NULL
);

-- Categories
CREATE TABLE Category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Features
CREATE TABLE Feature (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Purchase Requests
CREATE TABLE PurchaseRequest (
    id INT AUTO_INCREMENT PRIMARY KEY,
    purchaser_id INT NOT NULL,
    category_id INT NOT NULL,
    notes TEXT,
    status ENUM('pending', 'completed') DEFAULT 'pending',
    FOREIGN KEY (purchaser_id) REFERENCES User(id),
    FOREIGN KEY (category_id) REFERENCES Category(id)
);

-- Purchase Proposals
CREATE TABLE PurchaseProposal (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    technician_id INT NOT NULL,
    features TEXT,
    price DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    FOREIGN KEY (request_id) REFERENCES PurchaseRequest(id),
    FOREIGN KEY (technician_id) REFERENCES User(id)
);

-- Approved Purchases
CREATE TABLE ApprovedPurchase (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proposal_id INT NOT NULL,
    admin_id INT NOT NULL,
    approval_date DATE NOT NULL,
    FOREIGN KEY (proposal_id) REFERENCES PurchaseProposal(id),
    FOREIGN KEY (admin_id) REFERENCES User(id)
);

--Možná odstranit
-- Sample data
INSERT INTO User (username, password, role) VALUES
('admin', 'admin', 'admin'),
('technician', 'technician', 'technician'),
('purchaser', 'purchaser', 'purchaser');

INSERT INTO Category (name) VALUES ('Software'), ('Hardware');
INSERT INTO Feature (name) VALUES ('Fast Delivery'), ('Extended Warranty');
