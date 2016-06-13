CREATE TABLE "PROJECT_MEASURES" (
  "ID" BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "VALUE" DOUBLE,
  "METRIC_ID" INTEGER NOT NULL,
  "SNAPSHOT_ID" INTEGER,
  "RULE_ID" INTEGER,
  "RULES_CATEGORY_ID" INTEGER,
  "TEXT_VALUE" VARCHAR(4000),
  "TENDENCY" INTEGER,
  "MEASURE_DATE" TIMESTAMP,
  "PROJECT_ID" INTEGER,
  "ALERT_STATUS" VARCHAR(5),
  "ALERT_TEXT" VARCHAR(4000),
  "URL" VARCHAR(2000),
  "DESCRIPTION" VARCHAR(4000),
  "RULE_PRIORITY" INTEGER,
  "CHARACTERISTIC_ID" INTEGER,
  "PERSON_ID" INTEGER,
  "VARIATION_VALUE_1" DOUBLE,
  "VARIATION_VALUE_2" DOUBLE,
  "VARIATION_VALUE_3" DOUBLE,
  "VARIATION_VALUE_4" DOUBLE,
  "VARIATION_VALUE_5" DOUBLE,
  "MEASURE_DATA" BINARY(167772150),

  // new column, introduced in migration 1214
  "COMPONENT_UUID" VARCHAR(50)
);

CREATE INDEX "MEASURES_SID_METRIC" ON "PROJECT_MEASURES" ("SNAPSHOT_ID", "METRIC_ID");

CREATE INDEX "MEASURES_PERSON" ON "PROJECT_MEASURES" ("PERSON_ID");

CREATE TABLE "PROJECTS" (
  "ID" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "KEE" VARCHAR(400),
  "ROOT_ID" INTEGER,
  "UUID" VARCHAR(50),
  "PROJECT_UUID" VARCHAR(50),
  "MODULE_UUID" VARCHAR(50),
  "MODULE_UUID_PATH" VARCHAR(4000),
  "NAME" VARCHAR(2000),
  "DESCRIPTION" VARCHAR(2000),
  "ENABLED" BOOLEAN NOT NULL DEFAULT TRUE,
  "SCOPE" VARCHAR(3),
  "QUALIFIER" VARCHAR(10),
  "DEPRECATED_KEE" VARCHAR(400),
  "PATH" VARCHAR(2000),
  "LANGUAGE" VARCHAR(20),
  "COPY_RESOURCE_ID" INTEGER,
  "LONG_NAME" VARCHAR(2000),
  "PERSON_ID" INTEGER,
  "CREATED_AT" TIMESTAMP,
  "AUTHORIZATION_UPDATED_AT" BIGINT
);
