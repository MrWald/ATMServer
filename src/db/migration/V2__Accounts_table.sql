CREATE TABLE accounts (
	card_number VARCHAR(16) PRIMARY KEY,
	pin VARCHAR(4) NOT NULL,
	balance FLOAT NOT NULL,
	money_limit FLOAT NOT NULL,
	username VARCHAR(50) NOT NULL,
	FOREIGN KEY(username) REFERENCES users(username)
);