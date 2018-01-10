/**
  Sunshower application schema V1
  @author haswell
 */
 
 
create schema SUNSHOWER; 


/**
  references: io.sunshower.model.core.Version
  @author haswell
 */

CREATE TABLE SUNSHOWER.VERSION (
  id          bytea PRIMARY KEY,
  major       INTEGER,
  minor       INTEGER,
  minor_minor INTEGER,
  extension   VARCHAR(31)
);

/**
  references: io.sunshower.model.core.Application
  @author haswell

 */

CREATE TABLE SUNSHOWER.APPLICATION (
  id            bytea PRIMARY KEY,
  enabled       BOOLEAN,
  "name"        VARCHAR(255),
  instance_id   VARCHAR(255),
  location      VARCHAR(255), started_on    TIMESTAMP,
  last_shutdown TIMESTAMP,
  version_id    bytea,

  FOREIGN KEY (version_id) REFERENCES SUNSHOWER.VERSION (id)
);


/**
  references: io.sunshower.model.core.auth.Tenant
  @author haswell
 */

CREATE TABLE SUNSHOWER.TENANT (

  id            bytea PRIMARY KEY,
  parent_id     bytea,
  name          varchar(255) NOT NULL,

  FOREIGN KEY (parent_id) REFERENCES SUNSHOWER.TENANT(id)
);


/**
  references: io.sunshower.model.core.io.File
  @author haswell
 */
CREATE TABLE SUNSHOWER.FILE(
  id          bytea PRIMARY KEY,
  parent_id   bytea,
  path        varchar(255),
  extension   varchar(63),

  FOREIGN KEY (parent_id) REFERENCES SUNSHOWER.FILE(id)

);

/**
  references: io.sunshower.model.core.auth.TenantDetails

  @author haswell
 */

CREATE TABLE SUNSHOWER.TENANT_DETAILS (
  id            bytea PRIMARY KEY,
  tenant_id     bytea,
  root_id       bytea,

  FOREIGN KEY (root_id)   REFERENCES SUNSHOWER.FILE(id),
  FOREIGN KEY (tenant_id) references SUNSHOWER.TENANT(id)

);


/**
  references: io.sunshower.model.core.auth.User
  @author haswell
 */
 
CREATE TABLE SUNSHOWER.PRINCIPAL (
  id             bytea PRIMARY KEY,
  active         BOOLEAN DEFAULT FALSE,
  username       VARCHAR(255)  UNIQUE NOT NULL,
  password       VARCHAR(1024) NOT NULL,
  tenant_id      bytea,

  FOREIGN KEY (tenant_id) references SUNSHOWER.TENANT(id)
);


/**
  
  references: io.sunshower.model.core.auth
  @author haswell
 */

CREATE TABLE SUNSHOWER.USER_DETAILS (
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


  FOREIGN KEY (root_id)       REFERENCES SUNSHOWER.FILE(id),
  FOREIGN KEY (user_id)       REFERENCES SUNSHOWER.PRINCIPAL (id)
);

/**
  references: io.sunshower.model.core.auth.Role
  @author haswell
 */

CREATE TABLE SUNSHOWER.ROLE (
  id          bytea PRIMARY KEY,
  authority   VARCHAR(31) UNIQUE NOT NULL,
  description VARCHAR(255),
  parent_id   bytea,

  FOREIGN KEY (parent_id) REFERENCES SUNSHOWER.ROLE (id)
);



/**
  references: io.sunshower.service.signup
  @author haswell
 */
CREATE TABLE SUNSHOWER.REGISTRATION_REQUEST (
  id         bytea PRIMARY KEY,
  request_id VARCHAR(88) NOT NULL,
  requested  TIMESTAMP   NOT NULL DEFAULT now(),
  expires    TIMESTAMP   NOT NULL DEFAULT now(),
  user_id    bytea,

  FOREIGN KEY (user_id) REFERENCES SUNSHOWER.PRINCIPAL (id)
);


/**
  references: io.sunshower.model.core.auth.User.roles
  author: haswell
  
 */
CREATE TABLE SUNSHOWER.USERS_TO_ROLES (
  user_id bytea,
  role_id bytea,

  FOREIGN KEY (role_id) REFERENCES SUNSHOWER.ROLE (id),
  FOREIGN KEY (user_id) REFERENCES SUNSHOWER.PRINCIPAL (id)
);

/**
  references io.sunshower.model.core.auth.Permission
  author: haswell
 */

CREATE TABLE SUNSHOWER.PERMISSION (
  id          bytea PRIMARY KEY,
  name        VARCHAR(31),
  description VARCHAR(255)

);

/**
  references: io.sunshower.model.core.auth.Role.permissions
  author: haswell
 */

CREATE TABLE SUNSHOWER.ROLES_TO_PERMISSIONS (
  role_id       bytea,
  permission_id bytea,

  FOREIGN KEY (role_id) REFERENCES SUNSHOWER.ROLE (id),
  FOREIGN KEY (permission_id) REFERENCES SUNSHOWER.PERMISSION (id)
);


/**
  references: io.sunshower.model.core.auth.Credential
  author: haswell
 */
CREATE TABLE SUNSHOWER.CREDENTIAL (
  id                bytea NOT NULL PRIMARY KEY
);

/**
  references: io.sunshower.model.core.auth.Keypair
  author: haswell
 */
create table SUNSHOWER.KEYPAIR_CREDENTIAL (
  id                bytea NOT NULL PRIMARY KEY,
  key               TEXT NOT NULL,
  secret            TEXT NOT NULL
);

/**
  references: io.sunshower.model.core.auth.UsernamePasswordCredential
  author: haswell
 */

create table SUNSHOWER.USERNAME_PASSWORD_CREDENTIAL (
  id          bytea NOT NULL PRIMARY KEY,
  username    VARCHAR(255),
  password    VARCHAR(1024)
);


CREATE TABLE SUNSHOWER.GIT_LOCAL (
  id                      bytea NOT NULL PRIMARY KEY,
  file_id                 bytea,
  resolution_strategy     varchar(255) not null,

  FOREIGN KEY (file_id) REFERENCES SUNSHOWER.FILE(id)
);

CREATE TABLE SUNSHOWER.GIT_REMOTE (
  id              bytea NOT NULL PRIMARY KEY,
  name            varchar(255),
  credential_id   bytea,
  uri             varchar(1024) not null,

  FOREIGN KEY (credential_id) REFERENCES SUNSHOWER.CREDENTIAL(id)

);

CREATE TABLE SUNSHOWER.GIT_REPOSITORY (
  id            bytea NOT NULL PRIMARY KEY,

  local_id      bytea,

  remote_id     bytea,

  FOREIGN KEY (local_id) REFERENCES SUNSHOWER.GIT_LOCAL(id),
  FOREIGN KEY (remote_id) REFERENCES SUNSHOWER.GIT_REMOTE(id)
);



CREATE TABLE SUNSHOWER.WORKSPACE (
  id                     bytea                PRIMARY KEY,
  key                    VARCHAR(255)        NOT NULL ,
  name                   VARCHAR(255)        NOT NULL,
  classification         smallint,
  created                TIMESTAMP           NOT NULL DEFAULT now(),
  modified               TIMESTAMP           NOT NULL DEFAULT now(),

  repository_id bytea,

  FOREIGN KEY (repository_id) REFERENCES SUNSHOWER.GIT_REPOSITORY
);


create table SUNSHOWER.TEMPLATE_GRAPH (
  id              bytea primary key,
  name            varchar(255) not null,
  created         TIMESTAMP NOT NULL DEFAULT now(),
  modified        TIMESTAMP NOT NULL DEFAULT now()
    
);

create table SUNSHOWER.TEMPLATE_GRAPH_VERTEX (

  id              bytea primary key,
  graph_id        bytea,


  name            varchar(255) not null,
  created         TIMESTAMP NOT NULL DEFAULT now(),
  modified        TIMESTAMP NOT NULL DEFAULT now(),
  
  FOREIGN KEY (graph_id) REFERENCES SUNSHOWER.TEMPLATE_GRAPH(id)
);


create table SUNSHOWER.TEMPLATE_GRAPH_EDGE (
  id              bytea primary key,
  graph_id        bytea,


  name            varchar(255) not null,
  created         TIMESTAMP NOT NULL DEFAULT now(),
  modified        TIMESTAMP NOT NULL DEFAULT now(),
  FOREIGN KEY (graph_id) REFERENCES SUNSHOWER.TEMPLATE_GRAPH(id)
);





create table SUNSHOWER.TEMPLATE_LINK (

  id            bytea PRIMARY KEY,
  source_id     bytea not null,
  target_id     bytea not null,
  
  
  mode    smallint not null,
  type    smallint not null
  
);


create table SUNSHOWER.TEMPLATE  (
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

    FOREIGN KEY (version_id) REFERENCES SUNSHOWER.VERSION(id),
    FOREIGN KEY (graph_id) REFERENCES SUNSHOWER.TEMPLATE_GRAPH(id),
    FOREIGN KEY (workspace_id) REFERENCES SUNSHOWER.WORKSPACE(id)
  
);




-- Spring ACL schema


CREATE TABLE SUNSHOWER.acl_sid (
  id        bytea         NOT NULL PRIMARY KEY,
  principal BOOLEAN      NOT NULL,
  sid       VARCHAR(100) NOT NULL,
  CONSTRAINT unique_uk_1 UNIQUE (sid, principal)
);

CREATE TABLE SUNSHOWER.acl_class (
  id    bytea         NOT NULL PRIMARY KEY,
  class VARCHAR(100) NOT NULL,
  CONSTRAINT unique_uk_2 UNIQUE (class)
);

CREATE TABLE SUNSHOWER.acl_object_identity (
  id                 bytea PRIMARY KEY,
  object_id_class    bytea    NOT NULL,
  object_id_identity bytea    NOT NULL,
  parent_object      bytea,
  owner_sid          bytea,
  entries_inheriting BOOLEAN NOT NULL,
  CONSTRAINT unique_uk_3 UNIQUE (object_id_class, object_id_identity),
  CONSTRAINT foreign_fk_1 FOREIGN KEY (parent_object) REFERENCES SUNSHOWER.acl_object_identity (id),
  CONSTRAINT foreign_fk_2 FOREIGN KEY (object_id_class) REFERENCES SUNSHOWER.acl_class (id),
  CONSTRAINT foreign_fk_3 FOREIGN KEY (owner_sid) REFERENCES SUNSHOWER.acl_sid (id)
);

CREATE TABLE SUNSHOWER.acl_entry (
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
  REFERENCES SUNSHOWER.acl_object_identity (id),
  CONSTRAINT foreign_fk_5 FOREIGN KEY (sid) REFERENCES SUNSHOWER.acl_sid (id)
);

-- Spring security groups

CREATE TABLE SUNSHOWER.groups (
  id         bytea PRIMARY KEY,
  group_name VARCHAR(50) NOT NULL
);

CREATE TABLE SUNSHOWER.group_authorities (
  group_id  bytea        NOT NULL,
  authority VARCHAR(50) NOT NULL,
  CONSTRAINT fk_group_authorities_group FOREIGN KEY (group_id) REFERENCES SUNSHOWER.groups (id)
);

CREATE TABLE SUNSHOWER.group_members (
  id       bytea PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  group_id bytea        NOT NULL,
  CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES SUNSHOWER.groups (id)
);



--- HAL Schema





create table SUNSHOWER.ENTITY_TO_PROPERTIES (
  entity_id     bytea not null,
  property_id   bytea not null
);


create table SUNSHOWER.ENTITY_PROPERTIES (
  id              bytea primary key,
 
 
  name            varchar(255),
  
  type            varchar(255),
  property_type   char(1),
  property_key    varchar(255)
  
);

create table SUNSHOWER.STRING_PROPERTIES (
  id              bytea primary key,
  value           varchar(4096)
);


create table SUNSHOWER.INTEGER_PROPERTIES (
  id              bytea primary key,
  value           bigint 
);


  

