### Description
**Important: this project is only for demo purpose**

The flowlog project is using flowlog-service, kafka, spark and cassandra to aggregate the realtime log. The aggregation has one-minute delay, which means the log storage is base on minute level. However, the apis only support hourly level query, it is not a hard limitation, we can easily change it to minute level log query.

For more detail, refer the [Design Document](docs/Design.md), for the load test report, refer the [ab testing result](reports/ab_report.md)


### How-to
- Step 1, clone the repository and change directory to flowlog
- Step 2, run command to initialize the cassandra, kafka, spark
```bash
gradle init_servers
```
- Step 3, build and deploy the spark job
```bash
gradle submit_task
```
- Step 4, launch 2nd terminal and start the service
```bash
gradle service:bootRun
```
- Step 5, launch 3rd terminal and start the service
```bash
gradle tasks:bootRun
```
- Step 6, launch 4th terminal and send sample requests
```bash
# change the report_time to current time.
curl -vvv -XPOST \
  'http://localhost:8080/flows' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '[
  {
    "report_time": "2022-07-25T05:52:00Z",
    "src_app": "123",
    "desc_app": "456",
    "vpc_id": "dfg",
    "bytes_tx": 300,
    "bytes_rx": 200
  }
]'
# we already prepare some sample data during init servers (refer Step 2)
curl -vvv 'http://localhost:8080/flows?report_time=2022-07-25T05%3A52%3A00Z&agg_level=one_minute'
  
# after 1 minute run
curl -vvv 'http://localhost:8080/flows?report_time=2022-07-25T05%3A52%3A00Z&agg_level=one_minute'
```
- Step 7, stop the servers
```bash
gradle stop_servers
```
- Step 8 (optional), restart the servers
```bash
gradle start_servers
```

### Reference Documentation
For further reference, please consider the following sections:

* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#web.reactive)
* [Apache Spark](https://spark.apache.org/docs/latest/)
* [Apache Kafka](https://kafka.apache.org/documentation/)
* [Apache Cassandra](https://cassandra.apache.org/doc/latest/)
