package com.chamodh.RealtimeTicketingSystem.controllers;

import com.chamodh.RealtimeTicketingSystem.services.ConfigurationService;
import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin

public class ConfigurationController {

    @Autowired
    private ConfigurationService service;

    @PostMapping("/api/config/create")
    public void createConfigure(@RequestBody Configuration config){
        service.createConfig(config);
    }

    @GetMapping("/api/config")
    public Configuration getConfiguration(){
        return service.getConfig();
    }

    @GetMapping("/api/config/load")
    public Configuration loadFromFile(){
        return service.readConfigurationFile();
    }
}
