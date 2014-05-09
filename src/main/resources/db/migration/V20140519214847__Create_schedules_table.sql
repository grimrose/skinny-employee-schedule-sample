-- For H2 Database
create table schedules (
  id bigserial not null primary key,
  planned_schedule_id bigint not null,
  start timestamp not null,
  end timestamp,
  other_details varchar(512),
  created_at timestamp not null,
  updated_at timestamp not null
)
