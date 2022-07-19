## Background
To understand our services network connectivity, we monitor network “flows” (aggregated connection data) to understand how the network is used. Assume there are a set of agents on a large number of instances which monitor outbound connections and report them periodically. We need to implement a service which can 1) accept network flow data points via a write API, 2) aggregate those data points to accumulate the bytes transferred per flow and 3) serve the aggregated network flow data via a read API.

## Glossary
- source application (aka. src_app): the application that connect to the destination application
- destination application (aka. dest_app): the application that accept the connection from the source application
- vpc id (aka. vpc_id) : the identifier of virtual private network
- byte transmit (aka. byte_tx): the number of bytes send from src_app to dest_app
- byte receive (aka. byte_rx): the number of bytes received by src_app from desc_app

## Goal
### Assumption
- Assume we have 100 source application
- Assume ech source application connects to 100 different destination application
- Assume we have 5 VPCs for each src_app, dest_app pair.
- Assume we have 1,000,000 production instances only for source application, each app has 10,000 instances
- Assume the agent report the local aggregate log every minute
- Assume the size of the log entry are less than 1KB

### Out of Scope
- The logs are not strong consistency, they can be dropped/retransmitted a small amount during transmission
- The logs' aggregation are not realtime, they can be delayed in X (<=3) minutes.

## Architecture

**Architecture**

From the assumption section, each agent will send 1 request with 100 (src_app to dest_app flow) log entries per minute after aggregating locally. The http request would be send at begining of next the minute for all instance. If we can bare with 10 seconds latency, so the flowlog service's idea througph is around 1,000,000/10 rps, in this case, it is not possible to handle the requests in a single instance, we need at lease 30(assume each instance can handl 5,000 rps and 33% redundancy) instances for the flowlog service. Since each src_app connect to 100 dest_app and we have 1,000,000 instances, so the agent will generate 100 * 1,000,000 log entries per minute, it would be 6B entries per hour, if each log entry has 1KB, for every hour we need 6TB storage without aggregation. That's why in this design we add a kafka cluster and a spark cluster(Figure 1) to aggregate the log entries. 

When the flowlog service receives the http rquest from agent, it sends each log entry to the kafka cluster, the spark job will retrieve each log entry and aggregate them before save to the cassandra every minute. With the 1 minute aggregation time of the agent in mind and the 10 seconds transmission time from Agetn to flowlog service, tht total process time would be less than 3 minutes. Which means the customer who consume the log has to wait 3 minutes in total. After the aggregation in spark, the count of log entry per minute is 5 * 100 * 100 = 50,000 (5 distinct vpcs for each src_app, 100 src_app and 100 dest_app for each src_app). So the cassandra will store 60 * 50,000 * 1KB = 3GB , which is acceptable by considering the minute level delay and the storage size. If we only keep monthly hot data(in minute level), we only need less than 2.5 TB storage. 

```
┌───────────┐     ┌────────┐                   ┌────────┐  ┌───────────┐
│           │     │        │    ┌───────────┐  │        │  │           ├───┐
│   Agent   ├─────┤        │    │           ├──┤        ├──┤   Kafka   │   │
│           │     │        ├────┤  Flowlog  │  │        │  │           │   │
└───────────┘     │        │    │  Service  │  │        │  └───────────┘   │
                  │        │    │           │  │        │                  │
┌───────────┐     │        │    └───────────┘  │        │                  │
│           │     │        │                   │        │                  │
│   Agent   ├─────┤  Load  │                   │  Load  │                  │
│           │     │Balancer│                   │Balancer│                  │
└───────────┘     │        │    ┌───────────┐  │        │  ┌───────────┐   │
                  │        │    │           │  │        │  │           │   │
┌───────────┐     │        │    │  Flowlog  ├──┤        ├──┤   Kafka   │   │
│           │     │        ├────┤  Service  │  │        │  │           │   │
│   Agent   ├─────┤        │    │           │  │        │  └─────┬─────┘   │
│           │     │        │    └──────┬────┘  │        │        │         │
└───────────┘     └────────┘           │       └────────┘        │         │
                                       │                         │         │
                                       │                         │         │
                      ┌────────────────┴───────────┐       ┌─────┴────┐    │
                      │                            │       │          │    │
                      │         Load Balancer      ├───────┤  Spark   ├────┘
                      │                            │       │          │
                      └──────┬───────────────┬─────┘       └──────────┘
                             │               │
                      ┌──────┴────┐    ┌─────┴─────┐
                      │           │    │           │
                      │ Cassandra │    │ Cassandra │
                      │           │    │           │
                      └───────────┘    └───────────┘
                                                                               ┼
```
Figure 1, the overall architecture.

**Extensibility**

For cold data, we can keep 5 minutes level(e.g. 6 months data), hourly level(e.g. 1 year data) and daily level(e.g. 5-20 years data), we only need to extend to add another spark job (Figure 2) to read from cassandra table and save to another table, keep the same extend mechanism, we can use less than 10 TB storage and keep more than 20 years log entries.

```
               ┌───────────────────────────────┐
               │                               │
               │                               │
┌──────────────┴─────────────┐       ┌─────────┴───────────┐
│                            │       │                     │
│         Load Balancer      ├───────┤ Scheduled Spark Job │
│                            │       │                     │
└──────┬───────────────┬─────┘       └─────────────────────┘
       │               │
┌──────┴────┐    ┌─────┴─────┐
│           │    │           │
│ Cassandra │    │ Cassandra │
│           │    │           │
└───────────┘    └───────────┘
                        
```
Figure 2, scheduled spark job load from Cassandra, aggregate and save to cassandra

**Aggregate at GET method**

Since we haven't implemented the extensibility enhancement, so we need to aggregate the log entries from minute level to hour level in the hot table, that's why we need to aggregate during retrieving phase.

**Caching**

Since the data has 3 minutes delay, enable caching in the flowlog service would help the retrieval operation.

**Https/H2/H3**

Since the instance can keep the connection for long time, enable H2/H3 for streaming would reduce the TLS handshaking, it will saves the computing resource from the flowlog service. 

**Deployment**

We can deploy to any cloud since the architecture is not relying on any platform. We also be able to deploy them to the Kubernetes. If we choose to use AWS stack, we can replace the kafka with AWS kinisis, Spark with EMR, cassandra with AWS keyspace (minute level and 5 minutes level) and S3 (hourly and daily).

## APIs

**Message**
- src_app: string
- dest_app: string
- vpc_id: string
- bytes_tx: int
- bytes_rx: int
- hour: int

Example
```
{"hour":2,"src_app":"foo","desc_app":"bar","vpc_id":"vpc-0","bytes_tx":300,"bytes_rx":3000}
```

**Read API**
- Path: /flows
- Method: GET
- Query parameters
   - hour (int) - required

Example
```
curl -vvv http://localhost:8080/flows?hour=1
```

**Write API**
- Path: /flows
- Method: POST
- Request Body: a JSON array with Message spec

Example

```
curl -vvv -X POST http://localhost:8080/flows \
-H 'Content-Type: application/json' \
-d '[{"src_app": "foo", "dest_app": "bar", "vpc_id": "vpc-0", "bytes_tx":100, "bytes_rx": 500, "hour": 1}]'
```

**Health Check**
- Path: /ping
- Method: GET

Example
```
curl -v http://localhost:8080/ping
```

## Conclusion
This design will more fix for write intensive request with caching. By testing, the retrieve can easily handle 4,400 requests per second and log report can handl 18,000 requests per second with my local dev environment. For more info, check the reports in reports folder.


## References

* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#web.reactive)
* [Apache Spark](https://spark.apache.org/docs/latest/)
* [Apache Kafka](https://kafka.apache.org/documentation/)
* [Apache Cassandra](https://cassandra.apache.org/doc/latest/)
