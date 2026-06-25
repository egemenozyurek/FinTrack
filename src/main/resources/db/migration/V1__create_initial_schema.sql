-- V1__create_initial_schema.sql
-- Flyway bu dosyayı sadece bir kez çalıştırır.
-- Naming convention: V{versiyon}__{açıklama}.sql

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
                       id           BIGSERIAL PRIMARY KEY,
                       name         VARCHAR(100)  NOT NULL,
                       email        VARCHAR(150)  NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
                       updated_at   TIMESTAMP,

                       CONSTRAINT uk_users_email UNIQUE (email)
);

-- ============================================================
-- CATEGORIES
-- ============================================================
CREATE TABLE categories (
                            id         BIGSERIAL PRIMARY KEY,
                            name       VARCHAR(50)  NOT NULL,
                            type       VARCHAR(10)  NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
                            icon       VARCHAR(50),
                            color      VARCHAR(7),   -- #RRGGBB
                            created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
                            updated_at TIMESTAMP
);

-- ============================================================
-- TRANSACTIONS
-- ============================================================
CREATE TABLE transactions (
                              id               BIGSERIAL PRIMARY KEY,
                              user_id          BIGINT         NOT NULL,
                              category_id      BIGINT         NOT NULL,
                              amount           NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
                              type             VARCHAR(10)    NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
                              transaction_date DATE           NOT NULL,
                              note             VARCHAR(255),
                              receipt_image_url VARCHAR(500),
                              is_recurring     BOOLEAN        NOT NULL DEFAULT FALSE,
                              created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
                              updated_at       TIMESTAMP,

                              CONSTRAINT fk_transaction_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
                              CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);
CREATE INDEX idx_transactions_type      ON transactions(type);

-- ============================================================
-- DEBTS
-- ============================================================
CREATE TABLE debts (
                       id               BIGSERIAL PRIMARY KEY,
                       user_id          BIGINT         NOT NULL,
                       person_name      VARCHAR(100)   NOT NULL,
                       original_amount  NUMERIC(15, 2) NOT NULL CHECK (original_amount > 0),
                       remaining_amount NUMERIC(15, 2) NOT NULL,
                       type             VARCHAR(10)    NOT NULL CHECK (type IN ('LENT', 'BORROWED')),
                       status           VARCHAR(10)    NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PARTIAL', 'PAID')),
                       due_date         DATE,
                       note             VARCHAR(255),
                       created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
                       updated_at       TIMESTAMP,

                       CONSTRAINT fk_debt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_debts_user_status ON debts(user_id, status);
CREATE INDEX idx_debts_due_date    ON debts(due_date);

-- ============================================================
-- DEBT PAYMENTS
-- ============================================================
CREATE TABLE debt_payments (
                               id         BIGSERIAL PRIMARY KEY,
                               debt_id    BIGINT         NOT NULL,
                               amount     NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
                               paid_at    DATE           NOT NULL,
                               note       VARCHAR(255),
                               created_at TIMESTAMP      NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMP,

                               CONSTRAINT fk_payment_debt FOREIGN KEY (debt_id) REFERENCES debts(id) ON DELETE CASCADE
);

-- ============================================================
-- SEED DATA — Varsayılan kategoriler
-- Uygulama ilk açıldığında bunlar hazır gelsin.
-- ============================================================
INSERT INTO categories (name, type, icon, color) VALUES
                                                     ('Maaş',       'INCOME',  'cash-outline',           '#27AE60'),
                                                     ('Freelance',  'INCOME',  'laptop-outline',          '#2ECC71'),
                                                     ('Kira Geliri','INCOME',  'home-outline',            '#1ABC9C'),
                                                     ('Diğer Gelir','INCOME',  'add-circle-outline',      '#16A085'),
                                                     ('Market',     'EXPENSE', 'cart-outline',            '#E74C3C'),
                                                     ('Yemek',      'EXPENSE', 'fast-food-outline',       '#E67E22'),
                                                     ('Ulaşım',     'EXPENSE', 'car-outline',             '#3498DB'),
                                                     ('Kira',       'EXPENSE', 'home-outline',            '#9B59B6'),
                                                     ('Faturalar',  'EXPENSE', 'flash-outline',           '#F39C12'),
                                                     ('Sağlık',     'EXPENSE', 'medkit-outline',          '#1ABC9C'),
                                                     ('Giyim',      'EXPENSE', 'shirt-outline',           '#E91E63'),
                                                     ('Eğlence',    'EXPENSE', 'game-controller-outline', '#FF5722'),
                                                     ('Eğitim',     'EXPENSE', 'book-outline',            '#607D8B'),
                                                     ('Diğer',      'EXPENSE', 'ellipsis-horizontal-outline', '#95A5A6');