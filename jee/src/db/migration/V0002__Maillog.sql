create table mail_log (
    id serial not null primary key,
    recipient varchar(128) not null,
    subject text,
    success boolean not null,
    error_message text,
    created_at timestamp not null default current_timestamp,
);
