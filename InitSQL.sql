use OnlineChat;

drop table if exists UserInfo;
create table UserInfo(
	id int primary key not null auto_increment,
    username varchar(30) not null,
    password varchar(30) not null,
    firstname varchar(30),
    lastname varchar(30),
    phone varchar(30)
);

Insert into UserInfo(username, password, firstname, lastname, phone) values ('Aria', 'secret', 'Yuchen', 'Lin', '1234567890');
Insert into UserInfo(username, password, firstname, lastname, phone) values ('abc', 'secret', 'Aria', 'Lin', '1234567890');
Insert into UserInfo(username, password, firstname, lastname, phone) values ('def', 'secret', 'Jack', 'Lin', '1234567890');
Insert into UserInfo(username, password, firstname, lastname, phone) values ('qwe', 'secret', 'Rachel', 'Lin', '1234567890');
Insert into UserInfo(username, password, firstname, lastname, phone) values ('rrr', 'secret', 'Peter', 'Lin', '1234567890');

