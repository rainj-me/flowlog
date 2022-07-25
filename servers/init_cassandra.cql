create keyspace flowlog with replication = {'class':'SimpleStrategy', 'replication_factor': 1};
use flowlog;
create table message (
    agg_level text,
    report_time timestamp,
    uid uuid,
    src_app text,
    desc_app text,
    vpc_id text,
    bytes_tx int,
    bytes_rx int,
    status text,
    primary key(agg_level, report_time, uid)
) with clustering order by (report_time desc);
