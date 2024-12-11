package com.chamodh.RealtimeTicketingSystem.repositories;

import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepo extends MongoRepository <Configuration, Integer> {
}
