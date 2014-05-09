-- For H2 Database
create table employees (
  id bigserial not null primary key,
  first_name varchar(512) not null,
  middle_name varchar(512) not null,
  last_name varchar(512) not null,
  gender int not null,
  started_employment date,
  left_employment date,
  other_detail varchar(512),
  created_at timestamp not null,
  updated_at timestamp not null
)
