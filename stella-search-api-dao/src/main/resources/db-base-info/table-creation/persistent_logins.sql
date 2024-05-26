-- This is only used for PersistentTokenRepository of remember me service.
-- Source: org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl.CREATE_TABLE_SQL

create table persistent_logins
(
    username  varchar(64) not null,
    series    varchar(64) primary key,
    token     varchar(64) not null,
    last_used timestamp   not null
);