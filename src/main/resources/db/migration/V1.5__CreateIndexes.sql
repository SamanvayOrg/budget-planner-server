create unique index budget_financial_year_municipality_id_uindex
    on budget (financial_year, municipality_id);
create unique index city_class_name_uindex
    on city_class (name);
create unique index municipality_name_state_id_uindex
    on municipality (name, state_id);
create unique index state_name_uindex
    on state (name);

alter table budget
    alter column municipality_id set not null;
alter table login_user
    alter column municipality_id set not null;
alter table municipality
    alter column state_id set not null;
alter table municipality
    alter column class_id set not null;
alter table state
    alter column languages set not null;
alter table translation
    alter column language set not null;
alter table translation
    alter column value set not null;
alter table translation
    alter column state_id set not null;