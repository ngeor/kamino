CREATE TABLE IF NOT EXISTS repository (
  uuid varchar(100) not null,
  slug varchar(100) not null,
  primary key (uuid),
  unique key (slug)
);

CREATE TABLE IF NOT EXISTS user (
  uuid varchar(100) not null,
  username varchar(100) not null,
  primary key (uuid)
);

CREATE TABLE IF NOT EXISTS pull_request (
  id int not null,
  repository_uuid varchar(100) not null,
  author_uuid varchar(100) not null,
  created_on datetime not null,
  updated_on datetime not null,
  title varchar(200),
  primary key (id, repository_uuid),
  foreign key (repository_uuid) references repository (uuid),
  foreign key (author_uuid) references user (uuid)
);


CREATE TABLE IF NOT EXISTS pull_request_approver (
  id int not null,
  repository_uuid varchar(100) not null,
  approver_uuid varchar(100) not null,
  primary key (id, repository_uuid, approver_uuid),
  foreign key (id, repository_uuid) references pull_request (id, repository_uuid),
  foreign key (approver_uuid) references user (uuid)
);

CREATE TABLE IF NOT EXISTS pipeline (
  uuid varchar(100) not null,
  repository_uuid varchar(100) not null,
  creator_uuid varchar(100) not null,
  state varchar(50) not null,
  result varchar(50),
  created_on datetime not null,
  completed_on datetime,
  duration_in_seconds int not null,
  build_seconds_used int not null,
  trigger_name varchar(50) not null,
  target_ref_name varchar(100),
  primary key (uuid),
  foreign key (repository_uuid) references repository (uuid),
  foreign key (creator_uuid) references user (uuid)
);
