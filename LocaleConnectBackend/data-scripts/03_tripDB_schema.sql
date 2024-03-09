
CREATE TABLE IF NOT EXISTS trip (
                      id SERIAL PRIMARY KEY,
                      localguide_id INT NOT NULL,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      description TEXT,
                      departure_time DATE,
                      destination VARCHAR(255),
                      duration_in_hours INT,
                      capacity INT,
                      ratings_total DOUBLE PRECISION DEFAULT 0,
                      ratings_count INT DEFAULT 0,
                      average_rating DOUBLE PRECISION DEFAULT 0,
                      FOREIGN KEY (localguide_id) REFERENCES app_user(id)
);

CREATE TABLE IF NOT EXISTS trip_languages (
                                trip_id INT NOT NULL,
                                language VARCHAR(255),
                                FOREIGN KEY (trip_id) REFERENCES trip(id)
);

CREATE TABLE IF NOT EXISTS trip_daily_activities (
                                       trip_id INT NOT NULL,
                                       activity VARCHAR(255),
                                       FOREIGN KEY (trip_id) REFERENCES trip(id)
);

CREATE TABLE IF NOT EXISTS trip_places_to_visit (
                                      trip_id INT NOT NULL,
                                      place VARCHAR(255),
                                      FOREIGN KEY (trip_id) REFERENCES trip(id)
);

CREATE TABLE IF NOT EXISTS trip_image_urls (
                                 trip_id INT NOT NULL,
                                 image_url TEXT,
                                 FOREIGN KEY (trip_id) REFERENCES trip(id)
);

CREATE TABLE IF NOT EXISTS trip_attendees (
                                trip_id INT NOT NULL,
                                user_id INT NOT NULL,
                                FOREIGN KEY (trip_id) REFERENCES trip(id),
                                FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE IF NOT EXISTS trip_reviews (
                              trip_review_id SERIAL PRIMARY KEY,
                              trip_id INT NOT NULL,
                              user_id INT NOT NULL,
                              text TEXT,
                              timestamp TIMESTAMP,
                              FOREIGN KEY (trip_id) REFERENCES trip(id),
                              FOREIGN KEY (user_id) REFERENCES app_user(id)
);
