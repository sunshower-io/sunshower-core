/**
  Sunshower application schema V1
  @author haswell
 */


CREATE SCHEMA IF NOT EXISTS SUNSHOWER;


/**
  references: io.sunshower.model.core.Version
  @author haswell
 */

CREATE TABLE SUNSHOWER.VERSION (
  id          BYTEA PRIMARY KEY,
  major       INTEGER,
  minor       INTEGER,
  minor_minor INTEGER,
  extension   VARCHAR(31)
);


CREATE TABLE SUNSHOWER.PROPERTY(
    id    BYTEA PRIMARY KEY,
    property_key text,
    name         text,
);


CREATE TABLE SUNSHOWER.ENTITY_TO_PROPERTIES(
    entity_id       bytea,
    property_id     bytea,
    properties_key  text not null
);

/**
  references: io.sunshower.model.core.Application
  @author haswell

 */

CREATE TABLE SUNSHOWER.APPLICATION (
  id            BYTEA PRIMARY KEY,
  enabled       BOOLEAN,
  "name"        VARCHAR(255),
  instance_id   VARCHAR(255),
  location      VARCHAR(255),
  started_on    TIMESTAMP,
  last_shutdown TIMESTAMP,
  version_id    BYTEA,

  FOREIGN KEY (version_id) REFERENCES SUNSHOWER.VERSION (id)
);


/**
  references: io.sunshower.model.core.auth.Tenant
  @author haswell
 */

CREATE TABLE SUNSHOWER.TENANT (

  id        BYTEA PRIMARY KEY,
  parent_id BYTEA,
  name      VARCHAR(255) NOT NULL,

  FOREIGN KEY (parent_id) REFERENCES SUNSHOWER.TENANT (id)
);


/**
  references: io.sunshower.model.core.io.File
  @author haswell
 */
CREATE TABLE SUNSHOWER.FILE (
  id        BYTEA PRIMARY KEY,
  parent_id BYTEA,
  path      VARCHAR(255),
  extension VARCHAR(63),

  FOREIGN KEY (parent_id) REFERENCES SUNSHOWER.FILE (id)

);

/**
  references: io.sunshower.model.core.auth.TenantDetails

  @author haswell
 */

CREATE TABLE SUNSHOWER.TENANT_DETAILS (
  id        BYTEA PRIMARY KEY,
  tenant_id BYTEA,
  root_id   BYTEA,

  FOREIGN KEY (root_id) REFERENCES SUNSHOWER.FILE (id),
  FOREIGN KEY (tenant_id) REFERENCES SUNSHOWER.TENANT (id)

);

/**
  
  references: io.sunshower.model.core.auth
  @author haswell
 */

CREATE TABLE SUNSHOWER.USER_DETAILS (
  id            BYTEA PRIMARY KEY,
  firstname     VARCHAR(255),
  lastname      VARCHAR(255),
  phone_number  VARCHAR(63),
  registered    TIMESTAMP DEFAULT now(),
  last_active   TIMESTAMP,
  active_until  TIMESTAMP,
  email_address VARCHAR(255) UNIQUE NOT NULL,
  root_id       BYTEA,

  FOREIGN KEY (root_id) REFERENCES SUNSHOWER.FILE (id)
);


/**
  references: io.sunshower.model.core.auth.User
  @author haswell
 */

CREATE TABLE SUNSHOWER.PRINCIPAL (
  id         BYTEA PRIMARY KEY,
  active     BOOLEAN DEFAULT FALSE,
  username   VARCHAR(255) UNIQUE NOT NULL,
  password   VARCHAR(1024)       NOT NULL,
  tenant_id  BYTEA,
  details_id BYTEA,

  FOREIGN KEY (tenant_id) REFERENCES SUNSHOWER.TENANT (id),
  FOREIGN KEY (details_id) REFERENCES SUNSHOWER.USER_DETAILS (id)
);


/**
  references io.sunshower.service.security.Activation
  @author haswell
 */

CREATE TABLE SUNSHOWER.ACTIVATION (
  id              BYTEA PRIMARY KEY,
  active          BOOLEAN   DEFAULT FALSE,
  activation_date TIMESTAMP DEFAULT now(),
  activator_id    BYTEA,
  application_id  BYTEA,


  FOREIGN KEY (activator_id) REFERENCES SUNSHOWER.PRINCIPAL (id),
  FOREIGN KEY (application_id) REFERENCES SUNSHOWER.APPLICATION (id)

);


/**
  references: io.sunshower.model.core.auth.Role
  @author haswell
 */

CREATE TABLE SUNSHOWER.ROLE (
  id          BYTEA PRIMARY KEY,
  authority   VARCHAR(31) UNIQUE NOT NULL,
  description VARCHAR(255),
  parent_id   BYTEA,

  FOREIGN KEY (parent_id) REFERENCES SUNSHOWER.ROLE (id)
);


/**
  references: io.sunshower.service.signup
  @author haswell
 */
CREATE TABLE SUNSHOWER.REGISTRATION_REQUEST (
  id         BYTEA PRIMARY KEY,
  request_id VARCHAR(88) NOT NULL,
  requested  TIMESTAMP   NOT NULL DEFAULT now(),
  expires    TIMESTAMP   NOT NULL DEFAULT now(),
  user_id    BYTEA,

  FOREIGN KEY (user_id) REFERENCES SUNSHOWER.PRINCIPAL (id)
);


/**
  references: io.sunshower.model.core.auth.User.roles
  author: haswell
  
 */
CREATE TABLE SUNSHOWER.USERS_TO_ROLES (
  user_id BYTEA,
  role_id BYTEA,

  FOREIGN KEY (role_id) REFERENCES SUNSHOWER.ROLE (id),
  FOREIGN KEY (user_id) REFERENCES SUNSHOWER.PRINCIPAL (id)
);

/**
  references io.sunshower.model.core.auth.Permission
  author: haswell
 */

CREATE TABLE SUNSHOWER.PERMISSION (
  id          BYTEA PRIMARY KEY,
  name        VARCHAR(31),
  description VARCHAR(255)

);

/**
  references: io.sunshower.model.core.auth.Role.permissions
  author: haswell
 */

CREATE TABLE SUNSHOWER.ROLES_TO_PERMISSIONS (
  role_id       BYTEA,
  permission_id BYTEA,

  FOREIGN KEY (role_id) REFERENCES SUNSHOWER.ROLE (id),
  FOREIGN KEY (permission_id) REFERENCES SUNSHOWER.PERMISSION (id)
);


/**
  references: io.sunshower.model.core.auth.Credential
  author: haswell
 */
CREATE TABLE SUNSHOWER.CREDENTIAL (
  id BYTEA NOT NULL PRIMARY KEY
);

/**
  references: io.sunshower.model.core.auth.Keypair
  author: haswell
 */
CREATE TABLE SUNSHOWER.KEYPAIR_CREDENTIAL (
  id     BYTEA NOT NULL PRIMARY KEY,
  key    TEXT  NOT NULL,
  secret TEXT  NOT NULL
);

/**
  references: io.sunshower.model.core.auth.UsernamePasswordCredential
  author: haswell
 */

CREATE TABLE SUNSHOWER.USERNAME_PASSWORD_CREDENTIAL (
  id       BYTEA NOT NULL PRIMARY KEY,
  username VARCHAR(255),
  password VARCHAR(1024)
);


CREATE TABLE SUNSHOWER.GIT_LOCAL (
  id                  BYTEA        NOT NULL PRIMARY KEY,
  file_id             BYTEA,
  resolution_strategy VARCHAR(255) NOT NULL,

  FOREIGN KEY (file_id) REFERENCES SUNSHOWER.FILE (id)
);

CREATE TABLE SUNSHOWER.GIT_REMOTE (
  id            BYTEA         NOT NULL PRIMARY KEY,
  name          VARCHAR(255),
  credential_id BYTEA,
  uri           VARCHAR(1024) NOT NULL,

  FOREIGN KEY (credential_id) REFERENCES SUNSHOWER.CREDENTIAL (id)

);

CREATE TABLE SUNSHOWER.GIT_REPOSITORY (
  id        BYTEA NOT NULL PRIMARY KEY,

  local_id  BYTEA,

  remote_id BYTEA,

  FOREIGN KEY (local_id) REFERENCES SUNSHOWER.GIT_LOCAL (id),
  FOREIGN KEY (remote_id) REFERENCES SUNSHOWER.GIT_REMOTE (id)
);


CREATE TABLE SUNSHOWER.WORKSPACE (
  id             BYTEA PRIMARY KEY,
  key            VARCHAR(255) NOT NULL,
  name           VARCHAR(255) NOT NULL,
  classification SMALLINT,
  created        TIMESTAMP    NOT NULL DEFAULT now(),
  modified       TIMESTAMP    NOT NULL DEFAULT now(),

  repository_id  BYTEA,

  FOREIGN KEY (repository_id) REFERENCES SUNSHOWER.GIT_REPOSITORY
);


CREATE TABLE SUNSHOWER.TEMPLATE_GRAPH (
  id       BYTEA PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  created  TIMESTAMP    NOT NULL DEFAULT now(),
  modified TIMESTAMP    NOT NULL DEFAULT now()

);

CREATE TABLE SUNSHOWER.TEMPLATE_GRAPH_VERTEX (

  id       BYTEA PRIMARY KEY,
  graph_id BYTEA,


  name     VARCHAR(255) NOT NULL,
  created  TIMESTAMP    NOT NULL DEFAULT now(),
  modified TIMESTAMP    NOT NULL DEFAULT now(),

  FOREIGN KEY (graph_id) REFERENCES SUNSHOWER.TEMPLATE_GRAPH (id)
);


CREATE TABLE SUNSHOWER.TEMPLATE_GRAPH_EDGE (
  id       BYTEA PRIMARY KEY,
  graph_id BYTEA,


  name     VARCHAR(255) NOT NULL,
  created  TIMESTAMP    NOT NULL DEFAULT now(),
  modified TIMESTAMP    NOT NULL DEFAULT now(),
  FOREIGN KEY (graph_id) REFERENCES SUNSHOWER.TEMPLATE_GRAPH (id)
);


CREATE TABLE SUNSHOWER.TEMPLATE_LINK (

  id        BYTEA PRIMARY KEY,
  source_id BYTEA    NOT NULL,
  target_id BYTEA    NOT NULL,


  mode      SMALLINT NOT NULL,
  type      SMALLINT NOT NULL

);


CREATE TABLE SUNSHOWER.TEMPLATE (
  id           BYTEA PRIMARY KEY,
  version_id   BYTEA,
  workspace_id BYTEA,
  link_id      BYTEA,
  graph_id     BYTEA,

  key          VARCHAR(255),
  name         VARCHAR(255),
  description  VARCHAR(255),

  created      TIMESTAMP NOT NULL DEFAULT now(),
  modified     TIMESTAMP NOT NULL DEFAULT now(),

  FOREIGN KEY (version_id) REFERENCES SUNSHOWER.VERSION (id),
  FOREIGN KEY (graph_id) REFERENCES SUNSHOWER.TEMPLATE_GRAPH (id),
  FOREIGN KEY (workspace_id) REFERENCES SUNSHOWER.WORKSPACE (id)

);

-- Spring ACL schema


CREATE TABLE SUNSHOWER.acl_sid (
  id        BYTEA        NOT NULL PRIMARY KEY,
  principal BOOLEAN      NOT NULL,
  sid       VARCHAR(100) NOT NULL,
  CONSTRAINT unique_uk_1 UNIQUE (sid, principal)
);

CREATE TABLE SUNSHOWER.acl_class (
  id    BYTEA        NOT NULL PRIMARY KEY,
  class VARCHAR(100) NOT NULL,
  CONSTRAINT unique_uk_2 UNIQUE (class)
);

CREATE TABLE SUNSHOWER.acl_object_identity (
  id                 BYTEA PRIMARY KEY,
  object_id_class    BYTEA   NOT NULL,
  object_id_identity BYTEA   NOT NULL,
  parent_object      BYTEA,
  owner_sid          BYTEA,
  entries_inheriting BOOLEAN NOT NULL,
  CONSTRAINT unique_uk_3 UNIQUE (object_id_class, object_id_identity),
  CONSTRAINT foreign_fk_1 FOREIGN KEY (parent_object) REFERENCES SUNSHOWER.acl_object_identity (id),
  CONSTRAINT foreign_fk_2 FOREIGN KEY (object_id_class) REFERENCES SUNSHOWER.acl_class (id),
  CONSTRAINT foreign_fk_3 FOREIGN KEY (owner_sid) REFERENCES SUNSHOWER.acl_sid (id)
);

CREATE TABLE SUNSHOWER.acl_entry (
  id                  BYTEA PRIMARY KEY,
  acl_object_identity BYTEA   NOT NULL,
  ace_order           INT     NOT NULL,
  sid                 BYTEA   NOT NULL,
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
  id         BYTEA PRIMARY KEY,
  group_name VARCHAR(50) NOT NULL
);

CREATE TABLE SUNSHOWER.group_authorities (
  group_id  BYTEA       NOT NULL,
  authority VARCHAR(50) NOT NULL,
  CONSTRAINT fk_group_authorities_group FOREIGN KEY (group_id) REFERENCES SUNSHOWER.groups (id)
);

CREATE TABLE SUNSHOWER.group_members (
  id       BYTEA PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  group_id BYTEA       NOT NULL,
  CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES SUNSHOWER.groups (id)
);

--- HAL Schema


CREATE TABLE SUNSHOWER.ENTITY_TO_PROPERTIES (
  entity_id   BYTEA NOT NULL,
  property_id BYTEA NOT NULL,
  property_key VARCHAR(255)
);


CREATE TABLE SUNSHOWER.ENTITY_PROPERTIES (
  id            BYTEA PRIMARY KEY,


  name          VARCHAR(255),

  type          VARCHAR(255),
  property_type CHAR(1),
  property_key  VARCHAR(255)

);

CREATE TABLE SUNSHOWER.STRING_PROPERTIES (
  id    BYTEA PRIMARY KEY,
  value VARCHAR(4096)
);


CREATE TABLE SUNSHOWER.INTEGER_PROPERTIES (
  id    BYTEA PRIMARY KEY,
  value BIGINT
);


  

