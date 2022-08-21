create table size
(
	id   uuid primary key,
	code varchar(255) not null unique
);
create table crust
(
	id   uuid primary key,
	code varchar(255) not null unique
);
create table sauce
(
	id   uuid primary key,
	code varchar(255) not null unique
);
create table topping
(
	id   uuid primary key,
	code varchar(255) not null unique
);
create table "ORDER"
(
	id        uuid primary key,
	firstname varchar(255) not null,
	lastname  varchar(255) not null,
	email     varchar(255) not null
);
create table pizza
(
	id       uuid primary key,
	order_id uuid not null,
	size_id  uuid not null,
	crust_id uuid not null,
	sauce_id uuid not null,
	foreign key (order_id) references "ORDER" (id),
	foreign key (size_id) references size (id),
	foreign key (crust_id) references crust (id),
	foreign key (sauce_id) references sauce (id)
);

create table pizza_topping
(
	id         uuid primary key,
	pizza_id   uuid not null,
	topping_id uuid not null,
	foreign key (pizza_id) references pizza (id),
	foreign key (topping_id) references topping (id)
);
