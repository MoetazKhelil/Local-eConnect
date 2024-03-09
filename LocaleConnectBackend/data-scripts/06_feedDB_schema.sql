CREATE TABLE IF NOT EXISTS post (
                                    id SERIAL PRIMARY KEY,
                                    author_id BIGINT NOT NULL,
                                    date TIMESTAMP,
                                    content TEXT,
                                    post_type VARCHAR(50) NOT NULL,
                                    FOREIGN KEY (author_id) REFERENCES app_user(id),
                                    CHECK (post_type IN ('TRIP', 'ITINERARY', 'REGULAR', 'MEETUP'))
);

CREATE TABLE IF NOT EXISTS like_tab (
                                        id SERIAL PRIMARY KEY,
                                        post_id INT NOT NULL,
                                        liker_id BIGINT NOT NULL,
                                        FOREIGN KEY (post_id) REFERENCES post(id),
                                        FOREIGN KEY (liker_id) REFERENCES app_user(id)
);

CREATE TABLE IF NOT EXISTS comment (
                                       id SERIAL PRIMARY KEY,
                                       author_id BIGINT NOT NULL,
                                       date TIMESTAMP,
                                       text TEXT,
                                       post_id INT NOT NULL,
                                       FOREIGN KEY (author_id) REFERENCES app_user(id),
                                       FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE IF NOT EXISTS post_images (
                                           id SERIAL PRIMARY KEY,
                                           post_id INT NOT NULL,
                                           image_url TEXT,
                                           FOREIGN KEY (post_id) REFERENCES post(id)
);
