
create table SUNSHOWER.TEST_PROPERTY_ENTITY (

  id bytea not null primary key,
  name text,
  type text,
  visibility SMALLINT,
  created TIMESTAMP default current_time,
  modified TIMESTAMP DEFAULT current_time
);