CREATE TABLE items (
    uri          VARCHAR (1024) NOT NULL,
    content_type VARCHAR (256)  NOT NULL,
    data         BLOB (2G)      NOT NULL,

    PRIMARY KEY (uri)
);
