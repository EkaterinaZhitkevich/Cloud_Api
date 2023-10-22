--liquibase formatted sql

--changeset Zhitkevich.EYU:2

create table if not exists cloud_api.user(
    id bigserial primary key,
    user_uuid uuid unique not null,
    login varchar unique not null,
    password varchar unique not null
);

create table if not exists cloud_api.role(
    id bigserial primary key,
    role_name varchar unique not null
);

create table if not exists cloud_api.user_role(
    user_id bigint references cloud_api.user(id),
    role_id bigint references cloud_api.role(id)
);