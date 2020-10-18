ALTER TABLE user DROP COLUMN username;
ALTER TABLE user ADD COLUMN display_name varchar(100) NOT NULL UNIQUE;
