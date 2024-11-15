package com.chamodh.RealtimeTicketingSystem.services;

import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class ConfigurationService {

    private Configuration config;

    public void createConfig(Configuration config){
        this.config = config;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);
        try(FileWriter writer = new FileWriter("config.json")){
            writer.write(json);
            System.out.println("Configuration file saved to config.json");
        } catch (IOException e){
            System.out.println("Error saving JSON file." + e);
        }
    }

    public Configuration readConfigurationFile(){
        Gson gson = new Gson();
        try(FileReader reader = new FileReader("config.json")){
            config = gson.fromJson(reader, Configuration.class);
        } catch (IOException e){
            System.out.println("Error reading from file config.json" + e);
        }
        return config;
    }

    public Configuration getConfig(){
        return config;
    }
}
