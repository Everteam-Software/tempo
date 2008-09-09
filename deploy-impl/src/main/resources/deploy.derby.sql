
CREATE TABLE DEPLOY_ASSEMBLIES (
  ASSEMBLY VARCHAR(50),
  VERSION INTEGER,
  DIR VARCHAR(50),
  CACTIVE SMALLINT
);

CREATE TABLE DEPLOY_COMPONENTS (
  ASSEMBLY VARCHAR(50),
  VERSION INTEGER,
  COMPONENT VARCHAR(50),
  MANAGER VARCHAR(50),
  DIR VARCHAR(50)
);

CREATE TABLE DEPLOY_RESOURCES (
  ASSEMBLY VARCHAR(50),
  VERSION INTEGER,
  COMPONENT VARCHAR(50),
  RESOURCE VARCHAR(250)
);
