insert into state(id, name) values (1, 'Maharashtra');

insert into municipality (id, name, population, state_id) VALUES (1, 'Sinnar', '30000', 1);
insert into municipality (id, name, population, state_id) VALUES (2, 'Wai', '50000', 1);

insert into budget(id, financial_year, municipality_id) VALUES (1, 2020, 1);
insert into budget(id, financial_year, municipality_id) VALUES (2, 2021, 1);
insert into budget(id, financial_year, municipality_id) VALUES (3, 2022, 1);
insert into budget(id, financial_year, municipality_id) VALUES (4, 2023, 1);
insert into budget(id, financial_year, municipality_id) VALUES (5, 2020, 2);
insert into budget(id, financial_year, municipality_id) VALUES (6, 2021, 2);
insert into budget(id, financial_year, municipality_id) VALUES (7, 2022, 2);
insert into budget(id, financial_year, municipality_id) VALUES (8, 2023, 2);


select * from detailed_head;

insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (1, 1, 1, 1021, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (2, 1, 1, 1022, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (3, 1, 2, 1023, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (4, 1, 2, 1024, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (5, 1, 3, 1025, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (6, 1, 1, 1026, 1001.00, 1000.00, 900);




insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (7, 2, 1,  1021, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (8, 2, 1,  1022, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (9, 2, 2,  1023, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (10, 2, 2, 1024, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (11, 2, 3, 1025, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (12, 2, 1, 1026, 1001.00, 1000.00, 900);

insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (13, 3, 1, 1021, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (14, 3, 1, 1022, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (15, 3, 2, 1023, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (16, 3, 2, 1024, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (17, 3, 3, 1025, 1001.00, 1000.00, 900);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (18, 3, 1, 1026, 1001.00, 1000.00, 900);

insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (19, 4, 1, 1021, 1001.00, 0, 0);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (20, 4, 1, 1022, 1001.00, 0, 0);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (21, 4, 2, 1023, 1001.00, 0, 0);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (22, 4, 2, 1024, 1001.00, 0, 0);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (23, 4, 3, 1025, 1001.00, 0, 0);
insert into budget_line (id, budget_id, function_id, detailed_head_id, planned_amount, revised_amount, actual_amount) VALUES    (24, 4, 1, 1026, 1001.00, 0, 0);

insert into sample_budget_line
(state_id, function_id, detailed_head_id, display_order)
    (select 1, function_id, detailed_head_id, display_order
     from budget_line
     where budget_id = 1);
update budget_line set display_order=id;
select * from budget;
delete from budget_line where budget_id = 4;

insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (1, 1, (select id from function where full_code = '910'), (select id from detailed_head where full_code = '1110'), 1);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (2, 1, (select id from function where full_code = '910'), (select id from detailed_head where full_code = '1111'), 2);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (3, 1, (select id from function where full_code = '920'), (select id from detailed_head where full_code = '1120'), 3);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (4, 1, (select id from function where full_code = '920'), (select id from detailed_head where full_code = '1121'), 4);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (5, 1, (select id from function where full_code = '310'), (select id from detailed_head where full_code = '2510'), 5);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (6, 1, (select id from function where full_code = '314'), (select id from detailed_head where full_code = '2520'), 6);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (7, 1, (select id from function where full_code = '210'), (select id from detailed_head where full_code = '3221'), 7);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (8, 1, (select id from function where full_code = '315'), (select id from detailed_head where full_code = '4234'), 8);
insert into sample_budget_line(id, state_id, function_id, detailed_head_id, display_order)values (9, 1, (select id from function where full_code = '740'), (select id from detailed_head where full_code = '3230'), 9);
