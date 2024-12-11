package com.chamodh.RealtimeTicketingSystem.repositories;

import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * This ConfigRepo Class extends the MongoRepository interface to provide CRUD operations.
 * CRUD operations are ued to create and read the user configurations.
 */
@Repository
public interface ConfigRepo extends MongoRepository <Configuration, Integer> {
}
