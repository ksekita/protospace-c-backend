CREATE TABLE likes (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    prototype_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (prototype_id) REFERENCES prototypes(id) ON DELETE CASCADE,
    UNIQUE (user_id , prototype_id)
);