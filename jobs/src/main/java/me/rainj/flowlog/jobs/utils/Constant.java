package me.rainj.flowlog.jobs.utils;

/**
 * Constant
 */
public final class Constant {
    private Constant() {}

    /**
     * Kafka bootstrap server ip address.
     */
    public static final String KAFKA_BOOTSTRAP_SERVER_IP = "127.0.0.1";
    /**
     * Kafka bootstrap server port.
     */
    public static final String KAFKA_BOOTSTRAP_SERVER_PORT = "9092";
    /**
     * Cassandra keyspace.
     */
    public static final String KEYSPACE_NAME = "flowlog";
    /**
     * Cassandra's connection key.
     */
    public static final String CASSANDRA_CONNECTION_KEY = "spark.cassandra.connection.host";

    /**
     * Cassandra host ip address.
     */
    public static final String CASSANDRA_CONNECTION_HOST = "127.0.0.1";
}
