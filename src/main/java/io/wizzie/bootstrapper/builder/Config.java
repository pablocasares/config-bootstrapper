package io.wizzie.bootstrapper.builder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Config {
    Map<String, Object> config = new HashMap<>();
    Properties properties = new Properties();

    public Config(){
    }

    public Config(Map<String, Object> properties) {
        init(properties);
    }

    public Config(String configPath) throws IOException {
        init(configPath);
    }

    public Config(String configPath, Map<String, Object> properties) throws IOException {
        if (configPath != null) init(configPath);
        if (properties != null) init(properties);
    }

    private void init(String configPath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        init(objectMapper.readValue(new File(configPath), Map.class));
    }

    private void init(Map<String, Object> mapProperties) {
        config.putAll(mapProperties);
        properties.putAll(mapProperties);
    }


    public <T> T get(String property) {
        T ret = null;

        if (config != null) {
            ret = (T) config.get(property);
        }

        return ret;
    }

    public <T> T getOrDefault(String property, T defaultValue) {
        T ret = null;

        if (config != null && config.get(property) != null)
            ret = (T) config.get(property);
        else
            ret = defaultValue;

        return ret;
    }

    public Config put(String property, Object value) {
        config.put(property, value);
        properties.put(property, value);
        return this;
    }

    public Properties getProperties() {
        return properties;
    }
    public Map<String, Object> getMapConf() {
        return config;
    }


    public Config clone() {
        return new Config(new HashMap<>(config));
    }
}
