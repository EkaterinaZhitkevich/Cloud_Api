--liquibase formatted sql

--changeset Zhitkevich.EYU:3

create table if not exists cloud_api.file_metadata(
    id bigserial primary key,
    file_uuid uuid,
    filename varchar,
    extension varchar,
    size bigint,
    url varchar,
    hash varchar,
    user_id bigint references cloud_api.user(id)
)