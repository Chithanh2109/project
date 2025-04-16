-- Roles initialization
INSERT INTO roles (id, name) VALUES (1, 'ROLE_CUSTOMER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_THERAPIST');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_ADMIN');

-- Default admin user
INSERT INTO users (id, username, password, full_name, email, phone_number, address, user_type, is_active, created_at, updated_at)
VALUES (1, 'admin', '$2a$10$ZO.p6CfUvCQYKbWH/Z.qLuUwXAJYEzZZ0QUOwFfZcz0wB6xR1t7US', 'Admin User', 'admin@example.com', '0123456789', 'Admin Address', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Default therapist user
INSERT INTO users (id, username, password, full_name, email, phone_number, address, user_type, is_active, created_at, updated_at)
VALUES (2, 'therapist', '$2a$10$ZO.p6CfUvCQYKbWH/Z.qLuUwXAJYEzZZ0QUOwFfZcz0wB6xR1t7US', 'Therapist User', 'therapist@example.com', '0123456788', 'Therapist Address', 'THERAPIST', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Default customer
INSERT INTO users (id, username, password, full_name, email, phone_number, address, user_type, is_active, created_at, updated_at)
VALUES (3, 'customer', '$2a$10$ZO.p6CfUvCQYKbWH/Z.qLuUwXAJYEzZZ0QUOwFfZcz0wB6xR1t7US', 'Customer User', 'customer@example.com', '0123456787', 'Customer Address', 'CUSTOMER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User roles assignments
INSERT INTO user_roles (user_id, role_id) VALUES (1, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 1);

-- Sample services
INSERT INTO services (id, name, description, price, duration_minutes, is_active, created_at, updated_at)
VALUES (1, 'Basic Facial', 'A basic facial treatment for all skin types', 50.0, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO services (id, name, description, price, duration_minutes, is_active, created_at, updated_at)
VALUES (2, 'Deep Cleansing', 'Deep cleansing treatment for oily skin', 75.0, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO services (id, name, description, price, duration_minutes, is_active, created_at, updated_at)
VALUES (3, 'Anti-Aging Treatment', 'Special treatment to reduce signs of aging', 100.0, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample customers
INSERT INTO customers (id, user_id, skin_type, skin_concern, allergies, created_at, updated_at)
VALUES (1, 3, 'COMBINATION', 'ACNE', 'None', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 