insert into size (id, code)
values ('00000000-0000-0000-0000-000000000001', '25cm');
insert into size (id, code)
values ('00000000-0000-0000-0000-000000000002', '30cm');
insert into size (id, code)
values ('00000000-0000-0000-0000-000000000003', '35cm');
insert into size (id, code)
values ('00000000-0000-0000-0000-000000000004', '40cm');

insert into crust (id, code)
values ('00000000-0000-0000-0000-000000000001', 'classic');
insert into crust (id, code)
values ('00000000-0000-0000-0000-000000000002', 'italian');
insert into crust (id, code)
values ('00000000-0000-0000-0000-000000000003', 'cheesy-crust');

insert into sauce (id, code)
values ('00000000-0000-0000-0000-000000000001', 'bbq');
insert into sauce (id, code)
values ('00000000-0000-0000-0000-000000000002', 'white');
insert into sauce (id, code)
values ('00000000-0000-0000-0000-000000000003', 'red');

insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000001', 'pineapple');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000002', 'bacon');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000003', 'barbecue-swirl');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000004', 'mushrooms');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000005', 'mozzarella');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000006', 'swiss-cheese');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000007', 'garlic-sauce');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000008', 'grilled-ham');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000009', 'goat-cheese');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000010', 'gorgonzola');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000011', 'chicken');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000012', 'merguez');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000013', 'honey');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000014', 'kip-kebab');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000015', 'ground-beef');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000016', 'black-olives');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000017', 'onion');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000018', 'pizza-herbs');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000019', 'paprika');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000020', 'jalapenos');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000021', 'pepperoni');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000022', 'gorgonzola-cheese');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000023', 'prawn');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000024', 'spinach');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000025', 'fresh-tomato');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000026', 'tuna');
insert into topping (id, code)
values ('00000000-0000-0000-0000-000000000027', 'vegan-cheese');

insert into "ORDER" (id, firstname, lastname, email)
values ('00000000-0000-0000-0000-000000000001', 'Kristof', 'Neirynck', 'kristof@example.com');

insert into pizza (id, order_id, size_id, crust_id, sauce_id)
values ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
		'00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002',
		'00000000-0000-0000-0000-000000000003');

insert into pizza_topping (id, pizza_id, topping_id)
values ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
		'00000000-0000-0000-0000-000000000001');
insert into pizza_topping (id, pizza_id, topping_id)
values ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001',
		'00000000-0000-0000-0000-000000000004');
insert into pizza_topping (id, pizza_id, topping_id)
values ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001',
		'00000000-0000-0000-0000-000000000005');

