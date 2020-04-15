create table if not exists bookmark_years_months
(
    year   bigint       not null,
    month  bigint       not null,
    id     serial primary key,
    ym_key varchar(255) unique not null
);