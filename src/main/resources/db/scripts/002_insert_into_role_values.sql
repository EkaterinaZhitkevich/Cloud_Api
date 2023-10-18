--liquibase formatted sql

--changeset Zhitkevich.EYU:2


insert into authorization_service.role(role_name)
values ('USER'), ('ADMIN');
