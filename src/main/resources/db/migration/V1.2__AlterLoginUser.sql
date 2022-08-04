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

create table auth_role
(
    role_name varchar not null,
    role_id   varchar not null
        constraint auth_role_pk
            primary key
);

create unique index auth_role_role_id_uindex
    on auth_role (role_id);

create unique index auth_role_role_name_uindex
    on auth_role (role_name);

INSERT INTO public.auth_role (role_name, role_id)
VALUES ('Admin', 'rol_QxIxU5gJJm5D0VWb');

INSERT INTO public.auth_role (role_name, role_id)
VALUES ('Read-only', 'rol_vql4VKl503hiltt9');

INSERT INTO public.auth_role (role_name, role_id)
VALUES ('RegularUser', 'rol_E0MAcFsKwbhoxrsB');

INSERT INTO public.auth_role (role_name, role_id)
VALUES ('SuperAdmin', 'rol_3f6XNfNGGbV7e1nM');

