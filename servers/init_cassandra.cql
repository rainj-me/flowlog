create keyspace flowlog with replication = {'class':'SimpleStrategy', 'replication_factor': 1};
use flowlog;
create table flowlog (
    id uuid,
    src_app text,
    desc_app text,
    vpc_id text,
    bytes_tx int,
    bytes_rx int,
    hour int,
    primary key(hour, id)
);

insert into flowlog(id, src_app, desc_app, vpc_id, bytes_tx, bytes_rx, hour)
values(uuid(), 'foo', 'bar', 'vpc-0', 100, 500, 1);
insert into flowlog(id, src_app, desc_app, vpc_id, bytes_tx, bytes_rx, hour)
values(uuid(), 'foo', 'bar', 'vpc-0', 100, 1000, 2);
insert into flowlog(id, src_app, desc_app, vpc_id, bytes_tx, bytes_rx, hour)
values(uuid(), 'foo', 'bar', 'vpc-0', 100, 100, 1);
