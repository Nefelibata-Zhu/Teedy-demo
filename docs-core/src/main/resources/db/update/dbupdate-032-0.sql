create cached table T_USER_REGISTRATION_REQUEST (URR_ID_C varchar(36) not null, URR_USERNAME_C varchar(50) not null, URR_PASSWORD_C varchar(255) not null, URR_EMAIL_C varchar(100) not null, URR_CREATEDATE_D timestamp not null, URR_STATUS_C varchar(50) not null, primary key (URR_ID_C));

create index IDX_URR_USERNAME_C on T_USER_REGISTRATION_REQUEST (URR_USERNAME_C);
create index IDX_URR_STATUS_C on T_USER_REGISTRATION_REQUEST (URR_STATUS_C);

update T_CONFIG set CFG_VALUE_C = '32' where CFG_ID_C = 'DB_VERSION';