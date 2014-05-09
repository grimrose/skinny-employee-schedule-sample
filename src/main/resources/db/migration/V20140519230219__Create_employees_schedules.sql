create table employees_schedules (
  employee_id bigint not null,
  schedule_id bigint not null
);

alter table employees_schedules add primary key (employee_id, schedule_id);
alter table employees_schedules add foreign key(employee_id) references employees(id);
alter table employees_schedules add foreign key(schedule_id) references schedules(id);
