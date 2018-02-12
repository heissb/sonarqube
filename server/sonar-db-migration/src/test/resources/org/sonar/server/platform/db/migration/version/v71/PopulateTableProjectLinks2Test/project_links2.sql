CREATE TABLE "PROJECT_LINKS" (
  "ID" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "COMPONENT_UUID" VARCHAR(50),
  "LINK_TYPE" VARCHAR(20),
  "NAME" VARCHAR(128),
  "HREF" VARCHAR(2048) NOT NULL
);

CREATE TABLE "PROJECT_LINKS2" (
  "UUID" VARCHAR(40) NOT NULL PRIMARY KEY,
  "PROJECT_UUID" VARCHAR(50) NOT NULL,
  "LINK_TYPE" VARCHAR(20) NOT NULL,
  "NAME" VARCHAR(128),
  "HREF" VARCHAR(2048) NOT NULL,
  "CREATED_AT" BIGINT NOT NULL,
  "UPDATED_AT" BIGINT NOT NULL
);

CREATE TABLE "PROJECTS" (
  "ID" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "ORGANIZATION_UUID" VARCHAR(40) NOT NULL,
  "KEE" VARCHAR(400),
  "UUID" VARCHAR(50) NOT NULL,
  "UUID_PATH" VARCHAR(1500) NOT NULL,
  "ROOT_UUID" VARCHAR(50) NOT NULL,
  "PROJECT_UUID" VARCHAR(50) NOT NULL,
  "MODULE_UUID" VARCHAR(50),
  "MODULE_UUID_PATH" VARCHAR(1500),
  "MAIN_BRANCH_PROJECT_UUID" VARCHAR(50),
  "NAME" VARCHAR(2000),
  "DESCRIPTION" VARCHAR(2000),
  "PRIVATE" BOOLEAN NOT NULL,
  "TAGS" VARCHAR(500),
  "ENABLED" BOOLEAN NOT NULL DEFAULT TRUE,
  "SCOPE" VARCHAR(3),
  "QUALIFIER" VARCHAR(10),
  "DEPRECATED_KEE" VARCHAR(400),
  "PATH" VARCHAR(2000),
  "LANGUAGE" VARCHAR(20),
  "COPY_COMPONENT_UUID" VARCHAR(50),
  "LONG_NAME" VARCHAR(2000),
  "DEVELOPER_UUID" VARCHAR(50),
  "CREATED_AT" TIMESTAMP,
  "AUTHORIZATION_UPDATED_AT" BIGINT,
  "B_CHANGED" BOOLEAN,
  "B_COPY_COMPONENT_UUID" VARCHAR(50),
  "B_DESCRIPTION" VARCHAR(2000),
  "B_ENABLED" BOOLEAN,
  "B_UUID_PATH" VARCHAR(1500),
  "B_LANGUAGE" VARCHAR(20),
  "B_LONG_NAME" VARCHAR(500),
  "B_MODULE_UUID" VARCHAR(50),
  "B_MODULE_UUID_PATH" VARCHAR(1500),
  "B_NAME" VARCHAR(500),
  "B_PATH" VARCHAR(2000),
  "B_QUALIFIER" VARCHAR(10)
);
CREATE INDEX "PROJECTS_ORGANIZATION" ON "PROJECTS" ("ORGANIZATION_UUID");
CREATE UNIQUE INDEX "PROJECTS_KEE" ON "PROJECTS" ("KEE");
CREATE INDEX "PROJECTS_ROOT_UUID" ON "PROJECTS" ("ROOT_UUID");
CREATE UNIQUE INDEX "PROJECTS_UUID" ON "PROJECTS" ("UUID");
CREATE INDEX "PROJECTS_PROJECT_UUID" ON "PROJECTS" ("PROJECT_UUID");
CREATE INDEX "PROJECTS_MODULE_UUID" ON "PROJECTS" ("MODULE_UUID");
CREATE INDEX "PROJECTS_QUALIFIER" ON "PROJECTS" ("QUALIFIER");