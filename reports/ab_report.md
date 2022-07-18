### Load Test Reports
All the following test are base on my local desktop with spec
1. CPU: Intel I7-10700k, 8C, 16T
2. MEM: 16GB

**GET: Apache benchmark with 100,000 request and 1,000 concurrent threads**
- request per second: 4402
- max latency: 552 ms

```
Benchmarking localhost (be patient)
Completed 10000 requests
Completed 20000 requests
Completed 30000 requests
Completed 40000 requests
Completed 50000 requests
Completed 60000 requests
Completed 70000 requests
Completed 80000 requests
Completed 90000 requests
Completed 100000 requests
Finished 100000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /flows?hour=1
Document Length:        385 bytes

Concurrency Level:      1000
Time taken for tests:   22.713 seconds
Complete requests:      100000
Failed requests:        0
Total transferred:      48300000 bytes
HTML transferred:       38500000 bytes
Requests per second:    4402.85 [#/sec] (mean)
Time per request:       227.126 [ms] (mean)
Time per request:       0.227 [ms] (mean, across all concurrent requests)
Transfer rate:          2076.73 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   1.1      0      13
Processing:     8  226  57.6    215     552
Waiting:        2  226  57.5    215     552
Total:         19  226  57.5    215     552

Percentage of the requests served within a certain time (ms)
  50%    215
  66%    224
  75%    235
  80%    242
  90%    285
  95%    344
  98%    406
  99%    427
 100%    552 (longest request)
```

**POST: Apache benchmark with 100,000 request and 1,000 concurrent threads**
- rps: 18179
- max latency: 295ms

```
Benchmarking localhost (be patient)
Completed 10000 requests
Completed 20000 requests
Completed 30000 requests
Completed 40000 requests
Completed 50000 requests
Completed 60000 requests
Completed 70000 requests
Completed 80000 requests
Completed 90000 requests
Completed 100000 requests
Finished 100000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /flows
Document Length:        5 bytes

Concurrency Level:      1000
Time taken for tests:   5.501 seconds
Complete requests:      100000
Failed requests:        0
Total transferred:      11800000 bytes
Total body sent:        24400000
HTML transferred:       500000 bytes
Requests per second:    18179.66 [#/sec] (mean)
Time per request:       55.007 [ms] (mean)
Time per request:       0.055 [ms] (mean, across all concurrent requests)
Transfer rate:          2094.92 [Kbytes/sec] received
                        4331.87 kb/s sent
                        6426.79 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    5   5.2      4      34
Processing:     4   47  21.9     42     295
Waiting:        3   45  22.5     40     268
Total:          7   52  20.5     47     295

Percentage of the requests served within a certain time (ms)
  50%     47
  66%     55
  75%     61
  80%     65
  90%     77
  95%     88
  98%    103
  99%    128
 100%    295 (longest request)
 ```