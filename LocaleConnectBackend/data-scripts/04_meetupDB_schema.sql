
CREATE TABLE meetup (
                        id SERIAL PRIMARY KEY,
                        creator_id BIGINT NOT NULL,
                        name VARCHAR(255),
                        description TEXT,
                        date DATE,
                        start_time TIME,
                        end_time TIME,
                        cost DOUBLE PRECISION,
                        location VARCHAR(255),
                        ratings_total DOUBLE PRECISION DEFAULT 0,
                        ratings_count INT DEFAULT 0,
                        average_rating DOUBLE PRECISION DEFAULT 0
);

CREATE TABLE IF NOT EXISTS meetup_spoken_languages (
                                         meetup_id INT NOT NULL,
                                         language VARCHAR(255),
                                         FOREIGN KEY (meetup_id) REFERENCES meetup(id)
);

CREATE TABLE IF NOT EXISTS meetup_attendees (
                                  meetup_id INT NOT NULL,
                                  attendee_id BIGINT NOT NULL,
                                  FOREIGN KEY (meetup_id) REFERENCES meetup(id)
);
