CREATE TABLE IF NOT EXISTS app_user (
                                        id BIGSERIAL PRIMARY KEY,
                                        first_name VARCHAR(255) NOT NULL,
                                        last_name VARCHAR(255) NOT NULL,
                                        user_name VARCHAR(255) NOT NULL UNIQUE,
                                        email VARCHAR(255) NOT NULL UNIQUE,
                                        date_of_birth DATE,
                                        bio TEXT,
                                        password VARCHAR(255) NOT NULL,
                                        registered_as_local_guide BOOLEAN NOT NULL,
                                        profile_picture TEXT,
                                        city VARCHAR(255),
                                        ratings_total DOUBLE PRECISION DEFAULT 0,
                                        ratings_count INT DEFAULT 0,
                                        average_rating DOUBLE PRECISION DEFAULT 0,
                                        is_enabled BOOLEAN DEFAULT TRUE,
                                        dtype VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_followers (
                                              user_id BIGINT NOT NULL,
                                              follower_id BIGINT NOT NULL,
                                              PRIMARY KEY (user_id, follower_id),
                                              CONSTRAINT fk_user_followers_user_id FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                                              CONSTRAINT fk_user_followers_follower_id FOREIGN KEY (follower_id) REFERENCES app_user(id) ON DELETE CASCADE
);
