alter table login_user
    alter column user_name set not null;

alter table login_user
    add email varchar;

update login_user set email = user_name where id >= 1;

alter table login_user
    alter column email set not null;

create unique index login_user_email_uindex
    on login_user (email);

create unique index login_user_user_name_uindex
    on login_user (user_name);