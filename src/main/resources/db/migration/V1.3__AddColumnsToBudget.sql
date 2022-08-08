alter table budget
    add opening_balance numeric(15,2) default 0;

alter table budget
    add closing_balance numeric(15,2) default 0;

alter table budget
    add population integer not null default 0;

update budget b set population =  (select population from municipality m where m.id = b.municipality_id);

alter table municipality drop column population;