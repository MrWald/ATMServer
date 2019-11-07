create table accounts (
	card_number varchar(16) not null primary key,
	PIN varchar(4) not null,
	balance Double not null,
	money_limit Double not null,
	username varchar(50) not null,
	foreign key(username) references users(username)
);