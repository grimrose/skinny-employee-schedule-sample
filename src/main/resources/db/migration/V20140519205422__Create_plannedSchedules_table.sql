-- For H2 Database
create table planned_schedules (
  id bigserial not null primary key,
  name varchar(512) not null,
  description varchar(512),
  other_details varchar(512),
  created_at timestamp not null,
  updated_at timestamp not null
)
