package me.rainj.flowlog.jobs.utils;

public final class Constant {
    private Constant() {}
    public static final String KAFKA_BOOTSTRAP_SERVER_IP = "127.0.0.1";
    public static final String KAFKA_BOOTSTRAP_SERVER_PORT = "9092";
    public static final String KEYSPACE_NAME = "flowlog";
    public static final String CASSANDRA_CONNECTION_KEY = "spark.cassandra.connection.host";
    
    public static final String CASSANDRA_CONNECTION_HOST = "127.0.0.1";
}
