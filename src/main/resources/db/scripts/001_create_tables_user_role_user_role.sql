--liquibase formatted sql

--changeset Zhitkevich.EYU:1

create table authorization_service.user(
    id bigserial primary key,
    user_uuid uuid unique not null,
    login varchar unique not null,
    password varchar unique not null
);

create table authorization_service.role(
    id bigserial primary key,
    role_name varchar unique not null
);

create table authorization_service.user_role(
    user_id bigint references authorization_service.user(id),
    role_id bigint references authorization_service.role(id)
);