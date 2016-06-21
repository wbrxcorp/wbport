create table mail_log (
    id serial not null primary key,
    recipient varchar(128) not null,
    subject text,
    success boolean not null,
    error_message text,
    created_at timestamp not null default current_timestamp
);

create table system_config (
    config_key varchar(64) not null primary key,
    config_value text not null,
    created_at timestamp not null default current_timestamp,
    updated_at datetime
);

insert into system_config (config_key, config_value) values('confirm_email_subject','wbport.com 登録確認');
