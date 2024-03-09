
CREATE TABLE itinerary (
                           id SERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           name VARCHAR(255),
                           description TEXT,
                           number_of_days INT,
                           ratings_total DOUBLE PRECISION DEFAULT 0,
                           ratings_count INT DEFAULT 0,
                           average_rating DOUBLE PRECISION DEFAULT 0
);
CREATE TABLE itinerary_tags (
                                itinerary_id INT NOT NULL,
                                tag VARCHAR(255),
                                FOREIGN KEY (itinerary_id) REFERENCES itinerary(id)
);
CREATE TABLE itinerary_places_to_visit (
                                           itinerary_id INT NOT NULL,
                                           place VARCHAR(255),
                                           FOREIGN KEY (itinerary_id) REFERENCES itinerary(id)
);

CREATE TABLE itinerary_daily_activities (
                                            itinerary_id INT NOT NULL,
                                            activity VARCHAR(255),
                                            FOREIGN KEY (itinerary_id) REFERENCES itinerary(id)
);

CREATE TABLE IF NOT EXISTS itinerary_image_urls (
                                      itinerary_id INT NOT NULL,
                                      image_url TEXT,
                                      FOREIGN KEY (itinerary_id) REFERENCES itinerary(id)
);
CREATE TABLE IF NOT EXISTS itinerary_attendees (
                                     itinerary_id INT NOT NULL,
                                     attendee_id BIGINT NOT NULL,
                                     FOREIGN KEY (itinerary_id) REFERENCES itinerary(id),
                                     FOREIGN KEY (attendee_id) REFERENCES app_user(id) -- Assuming you have an app_user table from previous examples
);
