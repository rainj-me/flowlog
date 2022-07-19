package me.rainj.flowlog.service.repositories;

import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import me.rainj.flowlog.service.entities.Message;

/**
 * The message repository to fetch log entries from database
 */
@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, MapId> {
}
