package com.chamodh.RealtimeTicketingSystem.services;

import com.chamodh.RealtimeTicketingSystem.utils.Configuration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The ConfigurationService class handles the creation and reading of the configuration settings.
 * It uses Gson to serialize and deserialize configuration objects from JSON files.
 * The service allows program to save user defined configuration.
 * The service was used during the pre-stages of the program before implementing the database.
 */
@Service
public class ConfigurationService {

    private Configuration config;

    /**
     * Creates a new configuration and saves it to a JSON file. This method serializes the configuration
     * object to JSON format and writes it to a file named "config.json"
     * @param config the configuration object to be saved.
     */
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

    /**
     * Reads the configuration settings from a JSON file and returns the configuration object.
     * This method deserializes the JSON content from the file "config.json" into a Configuration object.
     * @return the configuration object read from the JSON file
     */
    public Configuration readConfigurationFile(){
        Gson gson = new Gson();
        try(FileReader reader = new FileReader("config.json")){
            config = gson.fromJson(reader, Configuration.class);
        } catch (IOException e){
            System.out.println("Error reading from file config.json" + e);
        }
        return config;
    }

    /**
     * Returns the current configuration object.
     * @return the current configuration object.
     */
    public Configuration getConfig(){
        return config;
    }
}
