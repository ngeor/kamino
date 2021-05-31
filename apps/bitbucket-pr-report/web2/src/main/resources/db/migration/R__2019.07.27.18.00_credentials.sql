CREATE TABLE IF NOT EXISTS credentials (
  owner varchar(100) not null,
  username varchar(100) not null,
  password varchar(100) not null,
  primary key (owner)
);

ALTER TABLE repository ADD COLUMN owner varchar(100) NOT NULL;
CREATE INDEX owner_idx ON repository(owner);
