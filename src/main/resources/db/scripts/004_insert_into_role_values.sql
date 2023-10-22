--liquibase formatted sql

--changeset Zhitkevich.EYU:4


insert into cloud_api.role(role_name)
values ('USER'), ('ADMIN');
