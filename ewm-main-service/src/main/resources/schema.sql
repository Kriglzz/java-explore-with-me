CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL,
    email VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    annotation VARCHAR NOT NULL,
    category_id BIGINT NOT NULL,
    create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit BIGINT NOT NULL,
    published_date TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR NOT NULL,
    title VARCHAR NOT NULL,
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_categories FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT fk_locations FOREIGN KEY (location_id) REFERENCES locations(location_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR NOT NULL,
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT unique_request UNIQUE (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT fk_compilation_events_event FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT fk_compilation_events_compilation FOREIGN KEY (compilation_id) REFERENCES compilations(compilation_id)
);