CREATE TABLE IF NOT EXISTS pipeline_importer_history (
  id identity not null,
  repository_uuid varchar(100) not null unique,
  last_checked_at timestamp not null,
  primary key (id),
  foreign key (repository_uuid) references repository (uuid)
);

CREATE TABLE IF NOT EXISTS pull_request_importer_history (
  id identity not null,
  repository_uuid varchar(100) not null unique,
  last_checked_at timestamp not null,
  primary key (id),
  foreign key (repository_uuid) references repository (uuid)
);

ALTER TABLE pull_request ADD COLUMN imported_at timestamp;
ALTER TABLE pull_request ADD COLUMN last_checked_at timestamp;

ALTER TABLE pipeline ADD COLUMN imported_at timestamp;
ALTER TABLE pipeline ADD COLUMN last_checked_at timestamp;
