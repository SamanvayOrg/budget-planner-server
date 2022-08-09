
alter table translation rename column  model_name to key;
alter table translation drop column model_id;

alter table translation
    add constraint unique_key_state_id_lang unique (key, state_id, language);
