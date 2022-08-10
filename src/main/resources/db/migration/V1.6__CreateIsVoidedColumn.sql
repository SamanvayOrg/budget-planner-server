alter table  auth_role          add is_voided boolean default false;
alter table  budget             add is_voided boolean default false;
alter table  budget_line        add is_voided boolean default false;
alter table  city_class         add is_voided boolean default false;
alter table  detailed_head      add is_voided boolean default false;
alter table  function           add is_voided boolean default false;
alter table  function_group     add is_voided boolean default false;
alter table  login_user         add is_voided boolean default false;
alter table  major_head         add is_voided boolean default false;
alter table  major_head_group   add is_voided boolean default false;
alter table  minor_head         add is_voided boolean default false;
alter table  municipality       add is_voided boolean default false;
alter table  sample_budget_line add is_voided boolean default false;
alter table  state              add is_voided boolean default false;
alter table  translation        add is_voided boolean default false;

DROP INDEX login_user_email_uindex;
DROP INDEX login_user_user_name_uindex;
DROP INDEX auth_role_role_id_uindex;
DROP INDEX auth_role_role_name_uindex;
ALTER TABLE translation DROP CONSTRAINT unique_key_state_id_lang;
DROP INDEX municipality_name_state_id_uindex;
DROP INDEX budget_financial_year_municipality_id_uindex;
DROP INDEX city_class_name_uindex;
DROP INDEX state_name_uindex;


CREATE UNIQUE INDEX login_user_email_uindex ON public.login_user USING btree (email, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX login_user_user_name_uindex ON public.login_user USING btree (user_name, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX auth_role_role_id_uindex ON public.auth_role USING btree (role_id, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX auth_role_role_name_uindex ON public.auth_role USING btree (role_name, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX translation_key_state_id_lang_uindex ON public.translation USING btree (key, state_id, language, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX municipality_name_state_id_uindex ON public.municipality USING btree (name, state_id, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX budget_financial_year_municipality_id_uindex ON public.budget USING btree (financial_year, municipality_id, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX city_class_name_uindex ON public.city_class USING btree (name, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
CREATE UNIQUE INDEX state_name_uindex ON public.state USING btree (name, is_voided)
    WHERE is_voided IS NULL OR is_voided = false;
