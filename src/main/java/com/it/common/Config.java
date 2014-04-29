package com.it.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static final String DEFAULT_PROPERTIES_NAME = "/server.properties";
    private static final String CATEGORY = "it.category";
    OptionParser parser = new OptionParser();
    OptionSpecBuilder clientOpt = parser.accepts("client",
            "If set, this is client.");

    // server, client (test)
    private boolean isClient;

    // config
    private String propertiesFileName;
    private Map<String, List<Server>> serverMap = Maps.newHashMap(); // category,server

    public Config(String[] args) throws FileNotFoundException, IOException {
        OptionSet options = parser.parse(args);
        isClient = options.has(clientOpt);
        loadProperties();
    }

    public void loadProperties() throws IOException, FileNotFoundException {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(DEFAULT_PROPERTIES_NAME));
        } catch (FileNotFoundException e) {
            LOGGER.error("property file({}) isn't existed.", DEFAULT_PROPERTIES_NAME);
        } catch (IOException e) {
            LOGGER.error("error to initialize properties", e);
        }

        // setting
        setCategory(getStringValue(properties, CATEGORY));
        for (String category : serverMap.keySet()) {
            setServer(category,
                    getStringValue(properties, CATEGORY + "." + category));
        }
    }

    public boolean isClient() {
        return isClient;
    }

    public void setClient(boolean isClient) {
        this.isClient = isClient;
    }

    public String getPropertiesFileName() {
        return propertiesFileName;
    }

    public void setPropertiesFileName(String fileName) {
        propertiesFileName = fileName;
    }

    private void setCategory(String categoryInProperties) {
        String[] categories = StringUtils.split(categoryInProperties, ",");
        for (String category : categories) {
            serverMap.put(category, new ArrayList<Server>());
        }
    }

    private void setServer(String category, String serverInProperties) {
        String[] hostPorts = StringUtils.split(serverInProperties, ",");
        for (String hostPort : hostPorts) {
            String[] split = StringUtils.split(hostPort, ":");
            serverMap.get(category).add(new Server(split[0], split[1]));
        }
    }

    private String getStringValue(Properties properties, String key,
            String defaultValue) {
        String result = defaultValue;
        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }
        try {
            result = properties.getProperty(key);
        } catch (Exception e) {
        }
        return result;
    }

    private String getStringValue(Properties properties, String key) {
        return getStringValue(properties, key, StringUtils.EMPTY);
    }
}