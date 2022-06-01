create table state
(
    id   serial primary key,
    name text
);

create table municipality
(
    id         serial primary key,
    name       varchar(255),
    population bigint,
    state_id   int references state (id)
);

create table budget
(
    id              serial primary key,
    financial_year  int,
    municipality_id bigint references municipality (id)
);

create table function_group
(
    id   serial primary key,
    code text,
    name text
);

create table function
(
    id                serial primary key,
    function_group_id integer references function_group (id),
    code              text,
    full_code         text,
    name              text
);

create table major_head_group
(
    id   serial primary key,
    code text not null,
    name text not null
);

create table major_head
(
    id                  serial primary key,
    code                text    not null,
    name                text    not null,
    major_head_group_id integer not null
        references major_head_group (id)
);

create table minor_head
(
    id            serial primary key,
    code          text    not null,
    name          text    not null,
    major_head_id integer not null references major_head (id)
);

create table detailed_head
(
    id            serial
        primary key,
    code          text    not null,
    name          text    not null,
    minor_head_id integer not null references minor_head (id),
    full_code     text    not null
);

create table login_user
(
    id              serial primary key,
    name            varchar(255),
    user_name       varchar(255),
    municipality_id int references municipality (id)
);

create table budget_line
(
    id               serial primary key,
    budget_id        int not null references budget (id),
    function_id      int not null references function (id),
    detailed_head_id int not null references detailed_head (id),
    planned_amount   numeric(15, 2),
    revised_amount   numeric(15, 2),
    actual_amount    numeric(15, 2),
    display_order    numeric(7, 2)
);

alter table budget_line
    add column display_order numeric(3, 2);

create table sample_budget_line
(
    id               serial primary key,
    state_id         integer not null references state (id),
    function_id      int     not null references function (id),
    detailed_head_id int     not null references detailed_head (id),
    display_order    numeric(7, 2)
);