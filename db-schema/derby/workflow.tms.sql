CREATE TABLE OPENJPA_SEQUENCE_TABLE (ID SMALLINT NOT NULL, SEQUENCE_VALUE BIGINT, PRIMARY KEY (ID));
CREATE TABLE tempo_acl -- ACL
    (id BIGINT NOT NULL, -- datastore id
    action VARCHAR(255), DTYPE VARCHAR(255), PRIMARY KEY (id));
CREATE TABLE tempo_acl_map (TASK_ID BIGINT, ELEMENT_ID BIGINT);
CREATE TABLE tempo_attachment -- Attachment
    (id BIGINT NOT NULL, -- datastore id
    payload_url VARCHAR(255), METADATA_ID BIGINT, PRIMARY KEY (id));
CREATE TABLE tempo_attachment_map (PATASK_ID BIGINT, ELEMENT_ID BIGINT);
CREATE TABLE tempo_attachment_meta -- AttachmentMetadata
    (id BIGINT NOT NULL, -- datastore id
    creation_date TIMESTAMP, description VARCHAR(255), file_name VARCHAR(255), mime_type VARCHAR(255), title VARCHAR(255), widget VARCHAR(255), PRIMARY KEY (id));
CREATE TABLE tempo_notification -- Notification
    (id BIGINT NOT NULL, failure_code VARCHAR(255), failure_reason VARCHAR(255), input_xml CLOB, priority INTEGER, state SMALLINT, PRIMARY KEY (id));
CREATE TABLE tempo_pa -- PATask
    (id BIGINT NOT NULL, complete_soap_action VARCHAR(255), deadline TIMESTAMP, failure_code VARCHAR(255), failure_reason VARCHAR(255), input_xml CLOB, is_chained_before SMALLINT, output_xml CLOB, previous_task_id VARCHAR(255), priority INTEGER, process_id VARCHAR(255), state SMALLINT, PRIMARY KEY (id));
CREATE TABLE tempo_pipa -- PIPATask
    (id BIGINT NOT NULL, init_message VARCHAR(255), init_soap VARCHAR(255), process_endpoint VARCHAR(255), PRIMARY KEY (id));
CREATE TABLE tempo_role (ACL_ID BIGINT, element VARCHAR(255), TASK_ID BIGINT);
CREATE TABLE tempo_task -- Task
    (id BIGINT NOT NULL, -- datastore id
    creation_date TIMESTAMP, description VARCHAR(255), form_url VARCHAR(255), task_idVARCHAR(255), internal_id INTEGER, PRIMARY KEY (id));
CREATE TABLE tempo_user (ACL_ID BIGINT, element VARCHAR(255), TASK_ID BIGINT);
CREATE INDEX I_TMPO_CL_DTYPE ON tempo_acl (DTYPE);
CREATE INDEX I_TMP__MP_ELEMENT ON tempo_acl_map (ELEMENT_ID);
CREATE INDEX I_TMP__MP_TASK_ID ON tempo_acl_map (TASK_ID);
CREATE INDEX I_TMP_MNT_METADATA ON tempo_attachment (METADATA_ID);
CREATE INDEX I_TMP__MP_ELEMENT1 ON tempo_attachment_map (ELEMENT_ID);
CREATE INDEX I_TMP__MP_PATASK_ID ON tempo_attachment_map (PATASK_ID);
CREATE INDEX I_TMP_ROL_ACL_ID ON tempo_role (ACL_ID);
CREATE INDEX I_TMP_ROL_TASK_ID ON tempo_role (TASK_ID);
CREATE INDEX I_TMP_USR_ACL_ID ON tempo_user (ACL_ID);
CREATE INDEX I_TMP_USR_TASK_ID ON tempo_user (TASK_ID);
