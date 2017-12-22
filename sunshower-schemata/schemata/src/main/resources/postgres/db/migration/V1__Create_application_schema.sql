CREATE TABLE VERSION (
  id          bytea PRIMARY KEY,
  major       INTEGER,
  minor       INTEGER,
  minor_minor INTEGER,
  extension   VARCHAR(31)
);

CREATE TABLE APPLICATION (
  id            bytea PRIMARY KEY,
  enabled       BOOLEAN,
  "name"        VARCHAR(255),
  instance_id   VARCHAR(255),
  location      VARCHAR(255), started_on    TIMESTAMP,
  last_shutdown TIMESTAMP,
  version_id    bytea,

  FOREIGN KEY (version_id) REFERENCES VERSION (id)
);



CREATE TABLE TENANT (

  id            bytea PRIMARY KEY,
  parent_id     bytea,
  name          varchar(255) NOT NULL,

  FOREIGN KEY (parent_id) REFERENCES TENANT(id)
);


CREATE TABLE FILE(
  id          bytea PRIMARY KEY,
  parent_id   bytea,
  path        varchar(255),
  extension   varchar(63),

  FOREIGN KEY (parent_id) REFERENCES FILE(id)

);

CREATE TABLE TENANT_DETAILS (
  id            bytea PRIMARY KEY,
  tenant_id     bytea,
  root_id       bytea,

  FOREIGN KEY (root_id)   REFERENCES FILE(id),
  FOREIGN KEY (tenant_id) references TENANT(id)

);

CREATE TABLE PRINCIPAL (
  id             bytea PRIMARY KEY,
  active         BOOLEAN DEFAULT FALSE,
  username       VARCHAR(255)  UNIQUE NOT NULL,
  password       VARCHAR(1024) NOT NULL,
  tenant_id      bytea,

  FOREIGN KEY (tenant_id) references TENANT(id)
);



CREATE TABLE USER_DETAILS (
  id            bytea PRIMARY KEY,
  firstname     VARCHAR(255),
  lastname      VARCHAR(255),
  phone_number  VARCHAR(63),
  registered    TIMESTAMP DEFAULT now(),
  last_active   TIMESTAMP,
  active_until  TIMESTAMP,
  email_address VARCHAR(255) UNIQUE NOT NULL,
  root_id       bytea,


  user_id       bytea,


  FOREIGN KEY (root_id)       REFERENCES FILE(id),
  FOREIGN KEY (user_id)       REFERENCES PRINCIPAL (id)
);

CREATE TABLE ROLE (
  id          bytea PRIMARY KEY,
  authority   VARCHAR(31) UNIQUE NOT NULL,
  description VARCHAR(255),
  parent_id   bytea,

  FOREIGN KEY (parent_id) REFERENCES ROLE (id)
);


CREATE TABLE REGISTRATION_REQUEST (
  id         bytea PRIMARY KEY,
  request_id VARCHAR(88) NOT NULL,
  requested  TIMESTAMP   NOT NULL DEFAULT now(),
  expires    TIMESTAMP   NOT NULL DEFAULT now(),
  user_id    bytea,

  FOREIGN KEY (user_id) REFERENCES PRINCIPAL (id)
);


CREATE TABLE USERS_TO_ROLES (
  user_id bytea,
  role_id bytea,

  FOREIGN KEY (role_id) REFERENCES ROLE (id),
  FOREIGN KEY (user_id) REFERENCES PRINCIPAL (id)
);


CREATE TABLE PERMISSION (
  id          bytea PRIMARY KEY,
  name        VARCHAR(31),
  description VARCHAR(255)

);

CREATE TABLE ROLES_TO_PERMISSIONS (
  role_id       bytea,
  permission_id bytea,

  FOREIGN KEY (role_id) REFERENCES ROLE (id),
  FOREIGN KEY (permission_id) REFERENCES PERMISSION (id)
);


CREATE TABLE CREDENTIAL (
  id                bytea NOT NULL PRIMARY KEY
);

create table KEYPAIR_CREDENTIAL (
  id                bytea NOT NULL PRIMARY KEY,
  key               TEXT NOT NULL,
  secret            TEXT NOT NULL
);


create table USERNAME_PASSWORD_CREDENTIAL (
  id          bytea NOT NULL PRIMARY KEY,
  username    VARCHAR(255),
  password    VARCHAR(1024)
);


CREATE TABLE GIT_LOCAL (
  id                      bytea NOT NULL PRIMARY KEY,
  file_id                 bytea,
  resolution_strategy     varchar(255) not null,

  FOREIGN KEY (file_id) REFERENCES FILE(id)
);

CREATE TABLE GIT_REMOTE (
  id              bytea NOT NULL PRIMARY KEY,
  name            varchar(255),
  credential_id   bytea,
  uri             varchar(1024) not null,

  FOREIGN KEY (credential_id) REFERENCES CREDENTIAL(id)

);

CREATE TABLE GIT_REPOSITORY (
  id            bytea NOT NULL PRIMARY KEY,

  local_id      bytea,

  remote_id     bytea,

  FOREIGN KEY (local_id) REFERENCES GIT_LOCAL(id),
  FOREIGN KEY (remote_id) REFERENCES GIT_REMOTE(id)
);



CREATE TABLE WORKSPACE (
  id                     bytea                PRIMARY KEY,
  key                    VARCHAR(255)        NOT NULL ,
  name                   VARCHAR(255)        NOT NULL,
  classification         smallint,
  created                TIMESTAMP           NOT NULL DEFAULT now(),
  modified               TIMESTAMP           NOT NULL DEFAULT now(),

  repository_id bytea,

  FOREIGN KEY (repository_id) REFERENCES GIT_REPOSITORY
);


create table TEMPLATE_GRAPH (
  id              bytea primary key,
  name            varchar(255) not null,
  created         TIMESTAMP NOT NULL DEFAULT now(),
  modified        TIMESTAMP NOT NULL DEFAULT now()
    
);

create table TEMPLATE_GRAPH_VERTEX (

  id              bytea primary key,
  graph_id        bytea,


  name            varchar(255) not null,
  created         TIMESTAMP NOT NULL DEFAULT now(),
  modified        TIMESTAMP NOT NULL DEFAULT now(),
  
  FOREIGN KEY (graph_id) REFERENCES TEMPLATE_GRAPH(id)
);


create table TEMPLATE_GRAPH_EDGE (
  id              bytea primary key,
  graph_id        bytea,


  name            varchar(255) not null,
  created         TIMESTAMP NOT NULL DEFAULT now(),
  modified        TIMESTAMP NOT NULL DEFAULT now(),
  FOREIGN KEY (graph_id) REFERENCES TEMPLATE_GRAPH(id)
);





create table ORCHESTRATION_TEMPLATE_LINK (

  id            bytea PRIMARY KEY,
  source_id     bytea not null,
  target_id     bytea not null,
  
  
  mode    smallint not null,
  type    smallint not null
  
);


create table ORCHESTRATION_TEMPLATE  (
    id            bytea PRIMARY KEY,
    version_id    bytea,
    workspace_id  bytea,
    link_id       bytea,
    graph_id      bytea,

    key           varchar(255),
    name          varchar(255),
    description   varchar(255),

    created  TIMESTAMP           NOT NULL DEFAULT now(),
    modified TIMESTAMP           NOT NULL DEFAULT now(),

    FOREIGN KEY (version_id) REFERENCES VERSION(id),
    FOREIGN KEY (graph_id) REFERENCES TEMPLATE_GRAPH(id),
    FOREIGN KEY (workspace_id) REFERENCES WORKSPACE(id)
  
);




-- Spring ACL schema


CREATE TABLE acl_sid (
  id        bytea         NOT NULL PRIMARY KEY,
  principal BOOLEAN      NOT NULL,
  sid       VARCHAR(100) NOT NULL,
  CONSTRAINT unique_uk_1 UNIQUE (sid, principal)
);

CREATE TABLE acl_class (
  id    bytea         NOT NULL PRIMARY KEY,
  class VARCHAR(100) NOT NULL,
  CONSTRAINT unique_uk_2 UNIQUE (class)
);

CREATE TABLE acl_object_identity (
  id                 bytea PRIMARY KEY,
  object_id_class    bytea    NOT NULL,
  object_id_identity bytea    NOT NULL,
  parent_object      bytea,
  owner_sid          bytea,
  entries_inheriting BOOLEAN NOT NULL,
  CONSTRAINT unique_uk_3 UNIQUE (object_id_class, object_id_identity),
  CONSTRAINT foreign_fk_1 FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
  CONSTRAINT foreign_fk_2 FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
  CONSTRAINT foreign_fk_3 FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
);

CREATE TABLE acl_entry (
  id                  bytea PRIMARY KEY,
  acl_object_identity bytea    NOT NULL,
  ace_order           INT     NOT NULL,
  sid                 bytea    NOT NULL,
  mask                INTEGER NOT NULL,
  granting            BOOLEAN NOT NULL,
  audit_success       BOOLEAN NOT NULL,
  audit_failure       BOOLEAN NOT NULL,
  CONSTRAINT unique_uk_4 UNIQUE (acl_object_identity, ace_order),
  CONSTRAINT foreign_fk_4 FOREIGN KEY (acl_object_identity)
  REFERENCES acl_object_identity (id),
  CONSTRAINT foreign_fk_5 FOREIGN KEY (sid) REFERENCES acl_sid (id)
);

-- Spring security groups

CREATE TABLE groups (
  id         bytea PRIMARY KEY,
  group_name VARCHAR(50) NOT NULL
);

CREATE TABLE group_authorities (
  group_id  bytea        NOT NULL,
  authority VARCHAR(50) NOT NULL,
  CONSTRAINT fk_group_authorities_group FOREIGN KEY (group_id) REFERENCES groups (id)
);

CREATE TABLE group_members (
  id       bytea PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  group_id bytea        NOT NULL,
  CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups (id)
);



--- HAL Schema



create table DOCKER_REGISTRY (

  id              bytea PRIMARY KEY,
  name            varchar(255) not null,
  url             varchar(2063) not null,
  content         bytea,

  hash_type       smallint,
  type            smallint,

  global          boolean,
  credential_id   bytea,

  foreign key (credential_id) REFERENCES CREDENTIAL(id)
);

CREATE INDEX
  DOCKER_REGISTRY_CONTENT ON
  DOCKER_REGISTRY(content)
;


create table ENTITY_TO_PROPERTIES (
  entity_id     bytea not null,
  property_id   bytea not null
);


create table ENTITY_PROPERTIES (
  id              bytea primary key,
 
 
  name            varchar(255),
  
  type            varchar(255),
  property_type   char(1),
  property_key    varchar(255)
  
);

create table STRING_PROPERTIES (
  id              bytea primary key,
  value           varchar(4096)
);


create table INTEGER_PROPERTIES (
  id              bytea primary key,
  value           bigint 
);


  

