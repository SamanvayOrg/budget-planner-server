insert into state(id, name) values (1, 'Maharashtra');

insert into city_class(name)values ('Municipal council');
insert into city_class(name)values ('Municipal corporation');
insert into city_class(name)values ('Nagar panchayat');

insert into municipality (id, name, population, state_id,class_id) VALUES (1, 'Sinnar', '30000', 1,1);
insert into municipality (id, name, population, state_id,class_id) VALUES (2, 'Wai', '50000', 1,1);

insert into budget(id, financial_year, municipality_id) VALUES (1, 2020, 1);
insert into budget(id, financial_year, municipality_id) VALUES (2, 2021, 1);
insert into budget(id, financial_year, municipality_id) VALUES (3, 2022, 1);
insert into budget(id, financial_year, municipality_id) VALUES (4, 2023, 1);
insert into budget(id, financial_year, municipality_id) VALUES (5, 2020, 2);
insert into budget(id, financial_year, municipality_id) VALUES (6, 2021, 2);
insert into budget(id, financial_year, municipality_id) VALUES (7, 2022, 2);
insert into budget(id, financial_year, municipality_id) VALUES (8, 2023, 2);

select * from detailed_head;
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (1, 1, 1, 1021, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (2, 1, 1, 1022, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (3, 1, 2, 1023, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (4, 1, 2, 1024, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (5, 1, 3, 1025, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (6, 1, 1, 1026, 1001.00, 1000.00, 900,200,700);




-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (7, 2, 1,  1021, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (8, 2, 1,  1022, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (9, 2, 2,  1023, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (10, 2, 2, 1024, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (11, 2, 3, 1025, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (12, 2, 1, 1026, 1001.00, 1000.00, 900,200,700);
--
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (13, 3, 1, 1021, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (14, 3, 1, 1022, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (15, 3, 2, 1023, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (16, 3, 2, 1024, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (17, 3, 3, 1025, 1001.00, 1000.00, 900,200,700);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (18, 3, 1, 1026, 1001.00, 1000.00, 900,200,700);
--
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (19, 4, 1, 1021, 1001.00, 0, 0,300,100);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (20, 4, 1, 1022, 1001.00, 0, 0,300,100);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (21, 4, 2, 1023, 1001.00, 0, 0,300,100);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (22, 4, 2, 1024, 1001.00, 0, 0,300,100);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (23, 4, 3, 1025, 1001.00, 0, 0,300,100);
-- insert into budget_line (id, budget_id, function_id, detailed_head_id, budgeted_amount, revised_amount, actual_amount,currentYear8MonthsActuals,currentYear4MonthsProbables) VALUES    (24, 4, 1, 1026, 1001.00, 0, 0,300,100);

insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (1,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '1110'),1, (select id from function where full_code = '910'), (select id from detailed_head where full_code = '1110'), 1);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (2,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '1111'),1, (select id from function where full_code = '910'), (select id from detailed_head where full_code = '1111'), 2);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (3,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '1120'),1, (select id from function where full_code = '920'), (select id from detailed_head where full_code = '1120'), 3);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (4,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '1121'),1, (select id from function where full_code = '920'), (select id from detailed_head where full_code = '1121'), 4);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (5,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '2510'),1, (select id from function where full_code = '310'), (select id from detailed_head where full_code = '2510'), 5);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (6,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '2520'),1, (select id from function where full_code = '314'), (select id from detailed_head where full_code = '2520'), 6);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (7,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '3221'),1, (select id from function where full_code = '210'), (select id from detailed_head where full_code = '3221'), 7);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (8,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '4234'),1, (select id from function where full_code = '315'), (select id from detailed_head where full_code = '4234'), 8);
insert into sample_budget_line(id,name, state_id, function_id, detailed_head_id, display_order)values (9,(select mh.name || ' - ' || detailed_head.name from detailed_head  inner join minor_head mh on detailed_head.minor_head_id = mh.id where full_code = '3230'),1, (select id from function where full_code = '740'), (select id from detailed_head where full_code = '3230'), 9);


insert into  city_class(name) values ('Municipal council');
insert into  city_class(name) values ('Municipal corporation');
insert into  city_class(name) values ('Nagar panchayat');

insert into login_user (id, name, user_name, municipality_id) VALUES (1, 'Vinay Sinnar', 'auth0|629077f1eba03f0069a9f8d9', 1);
insert into login_user (id, name, user_name, municipality_id) VALUES (1, 'Vinay Wai', 'auth0|628efd451ae4ca0069bcffa1', 2);

SELECT setval('state_id_seq', COALESCE((SELECT MAX(id)+1 FROM state), 1), false);
SELECT setval('municipality_id_seq', COALESCE((SELECT MAX(id)+1 FROM municipality), 1), false);
SELECT setval('budget_id_seq', COALESCE((SELECT MAX(id)+1 FROM budget), 1), false);
SELECT setval('budget_line_id_seq', COALESCE((SELECT MAX(id)+1 FROM budget_line), 1), false);
SELECT setval('sample_budget_line_id_seq', COALESCE((SELECT MAX(id)+1 FROM sample_budget_line), 1), false);
insert into translation (model_name, model_id, language, value) VALUES ('municipality', 1, 'mr', 'सिन्नर');