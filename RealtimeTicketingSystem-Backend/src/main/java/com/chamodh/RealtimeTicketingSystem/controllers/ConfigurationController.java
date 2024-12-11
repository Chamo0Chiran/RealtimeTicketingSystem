package com.chamodh.RealtimeTicketingSystem.controllers;

import com.chamodh.RealtimeTicketingSystem.repositories.ConfigRepo;
import com.chamodh.RealtimeTicketingSystem.services.ConfigurationService;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin

public class ConfigurationController {

    @Autowired
    private ConfigurationService service;

    @Autowired
    private ConfigRepo repo;


    @PostMapping("api/config/create")
    public Configuration createConfiguration(@RequestBody Configuration config){
        service.createConfig(config);
        return repo.save(config);
    }

    @GetMapping("/api/config")
    public Configuration getConfiguration(){
        long id = repo.count();
        List<Configuration> config = repo.findAll();
        return config.isEmpty() ? null : config.get((int) id - 1);

    }

    @GetMapping("/api/config/load")
    public Configuration loadFromFile(){
        return service.readConfigurationFile();
    }
}
