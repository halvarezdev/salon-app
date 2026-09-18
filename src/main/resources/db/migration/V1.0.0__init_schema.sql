-- Categorías
CREATE TABLE catalog_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    icon_url VARCHAR(500)
);

-- Servicios
CREATE TABLE catalog_services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    price NUMERIC(10, 2) NOT NULL,
    duration_minutes INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    category_id BIGINT REFERENCES catalog_categories(id) ON DELETE SET NULL
);

-- Clientes
CREATE TABLE booking_customers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL
);

-- Citas
CREATE TABLE booking_appointments (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES booking_customers(id),
    service_id BIGINT NOT NULL REFERENCES catalog_services(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    specialist_name VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    notes VARCHAR(1000),
    evidence_photo_url VARCHAR(1000),
    version BIGINT NOT NULL DEFAULT 0
);

-- Transacciones de pago
CREATE TABLE payment_transactions (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES booking_appointments(id),
    gateway_provider VARCHAR(50) NOT NULL,
    external_payment_id VARCHAR(100) UNIQUE,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'PEN',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    raw_payload TEXT
);

-- Datos iniciales (Seed)
INSERT INTO catalog_categories (name, description, icon_url) VALUES 
('Manicure', 'Cuidado y diseño integral de uñas de manos', '💅'),
('Pedicure', 'Tratamiento estético y spa para pies', '🦶'),
('Tratamientos Capilares', 'Hidratación, botox capilar y laceados', '💇‍♀️');

INSERT INTO catalog_services (name, description, price, duration_minutes, is_active, category_id) VALUES
('Manicure Rusa + Esmaltado Semipermanente', 'Limpieza profunda de cutículas y esmaltado de alta duración', 55.00, 60, true, 1),
('Uñas Acrílicas Esculpidas', 'Extensión de uñas esculpidas con acabado natural o diseño', 120.00, 120, true, 1),
('Pedicure Spa Completo', 'Exfoliación, hidratación profunda y masaje relajante', 70.00, 75, true, 2),
('Botox Capilar Reparador', 'Tratamiento reconstructor para cabello seco o maltratado', 150.00, 90, true, 3);
