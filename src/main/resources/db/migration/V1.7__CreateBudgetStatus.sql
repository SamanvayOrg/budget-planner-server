-- auto-generated definition
create table budget_status_audit
(
    id                    bigserial
        primary key,
    is_voided             boolean,
    created_at            timestamp,
    current_budget_status integer,
    prev_budget_status    integer,
    budget_id             bigint
        constraint fkh9and4y5vtj2w3q0yh6eyemkm
            references budget,
    user_id               bigint
        constraint fkd0ph2cgogfuvhilt798qmpidc
            references login_user
);

alter table budget_status_audit
    owner to budget_user;

INSERT INTO public.budget_status_audit
(is_voided, created_at, current_budget_status, prev_budget_status, budget_id, user_id)
SELECT false, '2022-08-20 11:10:14.000000', 0, 0, id, 6  FROM budget b;