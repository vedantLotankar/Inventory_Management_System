use INV_management;
show tables;
show databases;
-- 1. Drop `order_items` table first since it references `orders` and `products`
DROP TABLE IF EXISTS order_items;

-- 2. Drop `orders` table next since it references `users`
DROP TABLE IF EXISTS orders;

-- 3. Drop `products` table next since it is independent of `users`
DROP TABLE IF EXISTS products;

-- 4. Drop `users` table last
DROP TABLE IF EXISTS users;


-- 1. Create `users` Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- 2. Create `products` Table
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    stock_quantity INT NOT NULL,
    reorder_level INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);



-- 3. Create `orders` Table
CREATE TABLE orders (
    order_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    supplier_name VARCHAR(100),
    status ENUM('Pending', 'Completed', 'Cancelled') DEFAULT 'Pending'
);


-- 4. Create `order_items` Table
CREATE TABLE order_items (
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(100),
    quantity_ordered INT NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(12, 2) GENERATED ALWAYS AS (quantity_ordered * price_per_unit) STORED,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES allproducts(product_id) ON DELETE CASCADE
);

drop table order_items;

-- all products

CREATE TABLE allproducts (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO allproducts (name) VALUES
('Laptop'),
('Smartphone'),
('Headphones'),
('Monitor'),
('Keyboard'),
('Mouse'),
('Printer'),
('Tablet'),
('External Hard Drive');

drop table allproducts;
drop table order_items;

select * from products;






-- Insert into `users` table
INSERT INTO users (username, password) VALUES
('sai', '123'),
('ved', '456'),
('kkk', '789');

-- Insert into `products` table
INSERT INTO products (name, stock_quantity, reorder_level, price) VALUES
('Laptop', 15, 5, 55000.00),
('Smartphone', 30, 10, 20000.00),
('Headphones', 50, 20, 1500.00),
('Monitor', 10, 3, 12000.00),
('Keyboard', 40, 15, 800.00),
('Mouse', 100, 25, 500.00),
('Printer', 8, 2, 9000.00),
('Tablet', 20, 5, 25000.00),
('External Hard Drive', 25, 10, 4000.00);

-- Insert into `orders` table
INSERT INTO orders (user_id, product_name, supplier_name, status) VALUES
(1, 'Laptop', 'Supplier A', 'Pending'),
(2, 'Smartphone', 'Supplier B', 'Completed'),
(3, 'Headphones', 'Supplier C', 'Pending'),
(4, 'Monitor', 'Supplier D', 'Completed'),
(5, 'Keyboard', 'Supplier E', 'Cancelled'),
(6, 'Mouse', 'Supplier F', 'Pending'),
(7, 'Printer', 'Supplier G', 'Completed'),
(8, 'Tablet', 'Supplier H', 'Pending'),
(9, 'External Hard Drive', 'Supplier I', 'Cancelled');

select * from orders;
-- Insert into `order_items` table
INSERT INTO order_items (order_id, product_id, product_name, quantity_ordered, price_per_unit) VALUES
(1, 1, 'Laptop', 2, 55000.00),
(2, 2, 'Smartphone', 3, 20000.00),
(3, 3, 'Headphones', 5, 1500.00),
(4, 4, 'Monitor', 1, 12000.00),
(5, 5, 'Keyboard', 4, 800.00),
(6, 6, 'Mouse', 10, 500.00),
(7, 7, 'Printer', 1, 9000.00),
(8, 8, 'Tablet', 2, 25000.00),
(9, 9, 'External Hard Drive', 3, 4000.00);

INSERT INTO order_items (order_id, product_id, product_name, quantity_ordered, price_per_unit) VALUES
(1, 1, 'Laptop', 2, 55000.00),
(2, 2, 'Smartphone', 3, 20000.00),
(3, 3, 'Headphones', 5, 1500.00),
(4, 4, 'Monitor', 1, 12000.00),
(5, 5, 'Keyboard', 4, 800.00);

CREATE TABLE allproducts (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL
);

INSERT INTO allproducts (product_name) VALUES
('Laptop'),
('Smartphone'),
('Headphones'),
('Monitor'),
('Keyboard'),
('Mouse'),
('Printer'),
('Tablet'),
('External Hard Drive'),
('Pen'),
('Notebook'),
('Bottle'),
('Desk Chair'),
('Wireless Earbuds'),
('Gaming Console'),
('Graphics Card'),
('Smartwatch'),
('Router'),
('Projector'),
('Power Bank');


select * from allproducts;
drop table order_items;
select * from order_items;


select * from users;

select * from products;

select * from orders;

select * from order_items;




-- Queries


SELECT id, password FROM users WHERE username = 'sai';


SELECT product_id, user_id, name, stock_quantity, reorder_level, price FROM products;


-- orders

SELECT order_id, product_name, order_date, supplier_name, status FROM orders;

SELECT order_id, product_id, product_name, quantity_ordered, price_per_unit, total_price FROM order_items;

SELECT order_id, product_name, order_date, supplier_name, status FROM orders;

SELECT order_id, product_id, product_name, quantity_ordered, price_per_unit, total_price FROM order_items;

-- admin

SELECT product_id, user_id, name, stock_quantity, reorder_level, price FROM products;

-- product managmement

INSERT INTO products (user_id, name, stock_quantity, reorder_level, price) 
VALUES (2, 'Mechanical Keyboard', 75, 20, 89.99);


SET SQL_SAFE_UPDATES = 0;

UPDATE products 
SET stock_quantity = 60, reorder_level = 15, price = 129.99 
WHERE name = 'Mechanical Keyboard';
SELECT @@SQL_SAFE_UPDATES;

DELETE FROM products WHERE name = 'Mechanical Keyboard' AND user_id = 2;

SELECT name, stock_quantity, reorder_level, price FROM products;

SELECT name FROM products;


-- update delte order

UPDATE orders 
SET status = 'Completed' 
WHERE order_id = 5;

UPDATE order_items 
SET quantity_ordered = 7 
WHERE order_id = 3;

UPDATE products p
JOIN order_items oi ON p.product_id = oi.product_id
SET p.stock_quantity = p.stock_quantity - oi.quantity_ordered
WHERE oi.order_id = ?;


-- deleteorder

SELECT name FROM products WHERE product_id = 3;


SELECT COUNT(*) FROM orders WHERE product_name = 'Laptop';

INSERT INTO orders (product_name, order_date, supplier_name, status) 
VALUES ('Laptop', NOW(), 'DEll', 'Pending');

select * from orders;
select * from order_items;

INSERT INTO order_items (order_id, product_id, product_name, quantity_ordered, price_per_unit) VALUES (5,)







