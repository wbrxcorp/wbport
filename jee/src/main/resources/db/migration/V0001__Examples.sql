create table emp (
  id serial primary key,
  name varchar(255) not null,
  sal int not null
);

insert into emp(name,sal) values('shimarin', 102);
