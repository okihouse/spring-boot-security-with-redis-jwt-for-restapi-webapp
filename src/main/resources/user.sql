insert into USER(id, username, password) values(1, 'user', '$2a$11$BMHAHEws4Sz/SL5hhfLR/e45nehO.3RgV7j0v9mGaB9sYvEVHJb.e');
insert into USER(id, username, password) values(2, 'admin', '$2a$11$BMHAHEws4Sz/SL5hhfLR/e45nehO.3RgV7j0v9mGaB9sYvEVHJb.e');

insert into ROLE(role_id, id, role) values(1, 1, 'USER');
insert into ROLE(role_id, id, role) values(2, 2, 'ADMIN');

insert into ROLE_PERMISSION(permission_id, role_id, permission) values(1, 1, 'READ_MYPAGE');
insert into ROLE_PERMISSION(permission_id, role_id, permission) values(2, 2, 'READ_MYPAGE');
insert into ROLE_PERMISSION(permission_id, role_id, permission) values(3, 2, 'READ_ADMIN');
