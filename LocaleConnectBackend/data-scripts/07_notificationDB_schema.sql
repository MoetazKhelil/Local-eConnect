CREATE TABLE IF NOT EXISTS notification (
                              id BIGSERIAL PRIMARY KEY,
                              sender_id BIGINT NOT NULL,
                              receiver_id BIGINT NOT NULL,
                              sent_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                              title VARCHAR(255) DEFAULT 'New Notification',
                              message TEXT NOT NULL,
                              is_read BOOLEAN DEFAULT FALSE
);
