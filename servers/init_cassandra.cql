create keyspace flowlog with replication = {'class':'SimpleStrategy', 'replication_factor': 1};
use flowlog;
create table flowlog (
    report_time timestamp,
    id uuid,
    src_app text,
    desc_app text,
    vpc_id text,
    bytes_tx int,
    bytes_rx int,
    primary key(report_time, id)
);
