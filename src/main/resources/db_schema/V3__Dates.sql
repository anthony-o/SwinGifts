--liquibase formatted sql

--changeset anthony-o:3

ALTER TABLE EVENT ADD (
  CREATION_DATE     DATETIME,
  MODIFICATION_DATE DATETIME
);

ALTER TABLE PERSON ADD (
  CREATION_DATE     DATETIME,
  MODIFICATION_DATE DATETIME
);

ALTER TABLE WISH_LIST ADD (
  CREATION_DATE     DATETIME,
  MODIFICATION_DATE DATETIME
);

ALTER TABLE WISH_ITEM ADD (
  CREATION_DATE     DATETIME,
  MODIFICATION_DATE DATETIME
);

ALTER TABLE RESERVATION ADD CREATION_DATE DATETIME;