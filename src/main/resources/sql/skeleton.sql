drop table if exists documents;

CREATE TABLE documents
(
  id varchar(36) primary key NOT NULL,
  name character varying(255) NOT NULL,
  file_path character varying(255) NOT NULL,
  author character varying(255),
  description character varying(255),
  type character varying(255),
  created_date timestamp DEFAULT timestamp 'now ( )' NOT NULL

);