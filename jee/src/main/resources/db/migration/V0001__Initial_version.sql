create table users (
    id serial not null primary key,
    email varchar(64) not null unique,
    password varchar(128),
    nickname varchar(64),
    auth_token varchar(128) not null unique,
    auth_token_expires_at datetime not null,
    admin_user boolean not null default false,
    created_at timestamp not null default current_timestamp,
    updated_at datetime
);

create table servers (
    id serial not null primary key,
    fqdn varchar(128) not null unique,
    user_id bigint unsigned not null,
    foreign key(user_id) references users(id)
);

create table domains (
    domain_name varchar(64) not null primary key,
    user_id int
);
