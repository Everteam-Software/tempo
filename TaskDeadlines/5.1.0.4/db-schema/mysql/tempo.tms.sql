drop table IF EXISTS task_states;
drop table IF EXISTS task_types;
drop table IF EXISTS task_user_owners;
drop table IF EXISTS task_role_owners;
drop table IF EXISTS task_attachments;


CREATE TABLE task_states (
    id INT auto_increment,
    code VARCHAR (32) NOT NULL,
    description VARCHAR (128) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);

INSERT INTO task_states (id, code, description) VALUES (DEFAULT, 'ready', '');
INSERT INTO task_states (id, code, description) VALUES (DEFAULT, 'completed', '');
INSERT INTO task_states (id, code, description) VALUES (DEFAULT, 'failed', '');
INSERT INTO task_states (id, code, description) VALUES (DEFAULT, 'claimed', '');

CREATE TABLE task_types (
    id INT auto_increment,
    code VARCHAR (32) NOT NULL,
    description VARCHAR (128) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);

INSERT INTO task_types (id, code, description) VALUES (DEFAULT, 'init', '');
INSERT INTO task_types (id, code, description) VALUES (DEFAULT, 'activity', '');
INSERT INTO task_types (id, code, description) VALUES (DEFAULT, 'notification', '');

CREATE TABLE tasks (
    id INT auto_increment,
    task_id VARCHAR (128) NOT NULL,
    process_id VARCHAR (128),
    type_id INT NOT NULL
        REFERENCES task_types (id)
        ON DELETE RESTRICT,
    state_id INT NOT NULL
        REFERENCES task_states (id)
        ON DELETE RESTRICT,
    description VARCHAR (512),
    creation_date TIMESTAMP NOT NULL,
    form_url VARCHAR (2048) NOT NULL,
    failure_code VARCHAR (128),
    failure_reason VARCHAR (512),
    input_xml TEXT,
    output_xml TEXT,
    endpoint VARCHAR(256),
    namespace VARCHAR(512),
	init_soap_action VARCHAR(512),
	complete_soap_action VARCHAR(512),
  	is_chained_before CHAR(1),
  	previous_task_id VARCHAR (128),


    PRIMARY KEY (id),
    UNIQUE (task_id)
);

CREATE TABLE task_user_owners (
    task_id INT NOT NULL
        REFERENCES tasks (id)
        ON DELETE CASCADE,
    user_id VARCHAR (32) NOT NULL,

    PRIMARY KEY (task_id, user_id)
);

CREATE TABLE task_role_owners (
    task_id INT NOT NULL
        REFERENCES tasks (id)
        ON DELETE CASCADE,
    role_id VARCHAR (32) NOT NULL,

    PRIMARY KEY (task_id, role_id)
);

CREATE TABLE task_user_actions (
    task_id INT NOT NULL
        REFERENCES tasks (id)
        ON DELETE CASCADE,
    action_id VARCHAR (32) NOT NULL,
    user_id VARCHAR (32) NOT NULL,

    PRIMARY KEY (task_id, action_id, user_id)
);

CREATE TABLE task_role_actions (
    task_id INT NOT NULL
        REFERENCES tasks (id)
        ON DELETE CASCADE,
    action_id VARCHAR (32) NOT NULL,
    role_id VARCHAR (32) NOT NULL,

    PRIMARY KEY (task_id, action_id, role_id)
);

CREATE TABLE task_attachments (
    task_id INT NOT NULL
	REFERENCES tasks(id)
	ON DELETE CASCADE,
    payload_url VARCHAR (513) NOT NULL,
    file_name VARCHAR (513) NOT NULL,
    mime_type VARCHAR (128) NOT NULL,
    widget VARCHAR (32),
    creation_date TIMESTAMP,
    title VARCHAR (513),
    description VARCHAR (513),

    PRIMARY KEY (task_id, payload_url)
);

