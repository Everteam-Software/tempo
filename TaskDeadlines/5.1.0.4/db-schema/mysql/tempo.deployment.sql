drop table IF EXISTS items;

CREATE TABLE items (
    uri          VARCHAR (1000) NOT NULL,
    content_type VARCHAR (256)  NOT NULL,
    data         LONGBLOB       NOT NULL,

    PRIMARY KEY (uri(767))
);
