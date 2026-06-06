CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(200) NOT NULL,
    publication_year INT NOT NULL,
    genre VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS book_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    order_comment VARCHAR(500),
    is_urgent BOOLEAN NOT NULL DEFAULT FALSE,
    payment_method VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    country VARCHAR(255),
    city VARCHAR(255),
    street VARCHAR(255),
    building_number VARCHAR(255),
    apartment_number VARCHAR(255),
    postal_code VARCHAR(255),
    additional_details VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS book_order_books (
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, book_id),
    FOREIGN KEY (order_id) REFERENCES book_orders(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);
