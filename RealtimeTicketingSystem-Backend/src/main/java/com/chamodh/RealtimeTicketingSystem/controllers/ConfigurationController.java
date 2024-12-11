package com.chamodh.RealtimeTicketingSystem.controllers;

import com.chamodh.RealtimeTicketingSystem.repositories.ConfigRepo;
import com.chamodh.RealtimeTicketingSystem.services.ConfigurationService;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
/**
 * The ConfigurationController class handles HTTP requests related to the configuration management.
 * It provides endpoints for creating, retrieving and loading configuration settings to the server.
 * The class uses Configuration service and Configuration Repository classes for performing operations.
 */
public class ConfigurationController {

    @Autowired
    private ConfigurationService service;

    @Autowired
    private ConfigRepo repo;

    /**
     * Mapping for creating a new configurations
     * @param config the configuration object received from the request body.
     * @return the configuration object to save on database through repo.
     */
    @PostMapping("api/config/create")
    public Configuration createConfiguration(@RequestBody Configuration config){
        service.createConfig(config);
        return repo.save(config);
    }

    /**
     * Retrieves the most recent configuration from the database through repository methods.
     * @return the most recent configuration as in Object format.
     */
    @GetMapping("/api/config")
    public Configuration getConfiguration(){
        long id = repo.count();
        List<Configuration> config = repo.findAll();
        return config.isEmpty() ? null : config.get((int) id - 1);

    }

//    /**
//     * Loads configuration from the saved file.
//     * Used before the implementation of Mongodb (database).
//     * @return the most recent configuration object.
//     */
//    @GetMapping("/api/config/load")
//    public Configuration loadFromFile(){
//        return service.readConfigurationFile();
//    }
}
