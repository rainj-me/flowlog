package me.rainj.flowlog.service.repositories;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import me.rainj.flowlog.service.entities.Message;
import reactor.core.publisher.Flux;

import java.time.Instant;

/**
 * The message repository to fetch log entries from database
 */
@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, String> {

    @Query("SELECT * FROM message WHERE agg_level = ?0 AND report_time = ?1")
    Flux<Message> findAllByAggLevelAndReportTime(String aggLevel, Instant reportTime);
}
