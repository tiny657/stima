package com.it.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String DEFAULT_PROPERTIES_NAME = "/server.properties";
    private static final String CATEGORY = "server.category";
    private static final String HOST = "host";
    private static final String PORT = "port";

    private OptionParser parser = new OptionParser();
    private ArgumentAcceptingOptionSpec<String> hostOpt = parser
            .accepts("host", "this host").withOptionalArg()
            .ofType(String.class).defaultsTo(StringUtils.EMPTY);
    private ArgumentAcceptingOptionSpec<Integer> portOpt = parser
            .accepts("port", "this port").withOptionalArg()
            .ofType(Integer.class).defaultsTo(0);

    private String host;
    private int port;

    public Config(String[] args) throws FileNotFoundException, IOException,
            Exception {
        OptionSet options = parser.parse(args);
        setHost(options.valueOf(hostOpt));
        setPort(options.valueOf(portOpt));
        loadProperties();
        if (!validate()) {
            throw new Exception(host + ":" + port + " isn't valid.");
        }
    }

    public void loadProperties() throws IOException, FileNotFoundException {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(
                    DEFAULT_PROPERTIES_NAME));
        } catch (FileNotFoundException e) {
            logger.error("property file({}) isn't existed.",
                    DEFAULT_PROPERTIES_NAME);
        } catch (IOException e) {
            logger.error("error to initialize properties", e);
        }

        // setting
        if (host.equals(StringUtils.EMPTY)) {
            setHost(getStringValue(properties, HOST));
        }
        if (port == 0) {
            setPort(getIntegerValue(properties, PORT));
        }
        setCategory(getStringValue(properties, CATEGORY));
        for (String category : AllServer.getInstance().getCategories()
                .keySet()) {
            setServer(category,
                    getStringValue(properties, CATEGORY + "." + category));
        }
        logger.info(AllServer.getInstance().toString());
    }

    public List<Server> getServers() {
        return AllServer.getInstance().getServers();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private boolean validate() {
        for (Server server : getServers()) {
            if (server.getHost().equals(host) && server.getPort() == port) {
                return true;
            }
        }
        return false;
    }

    private void setCategory(String categoryInProperties) {
        String[] categories = StringUtils.split(categoryInProperties, ",");
        for (String category : categories) {
            AllServer.getInstance().addCategory(category);
        }
    }

    private void setServer(String category, String serverInProperties) {
        String[] hostPorts = StringUtils.split(serverInProperties, ",");
        for (String hostPort : hostPorts) {
            String[] splitedHostPort = StringUtils.split(hostPort, ":");
            AllServer.getInstance().addServer(category,
                    new Server(splitedHostPort[0], splitedHostPort[1]));
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

    private Boolean getBooleanValue(Properties properties, String key) {
        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }
        return Boolean.valueOf(properties.getProperty(key));
    }

    private Boolean getBooleanValue(Properties properties, String key,
            boolean defaultValue) {
        try {
            return getBooleanValue(properties, key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Integer getIntegerValue(Properties properties, String key,
            int defaultValue) {
        try {
            Integer intValue = getIntegerValue(properties, key);
            return intValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Integer getIntegerValue(Properties properties, String key) {
        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }
        return Integer.valueOf(properties.getProperty(key));
    }

    private Long getLongValue(Properties properties, String key,
            Long defaultValue) {
        try {
            Long longValue = getLongValue(properties, key);
            return longValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private Long getLongValue(Properties properties, String key) {
        if (properties == null) {
            throw new IllegalArgumentException("properties is null");
        }
        return Long.valueOf(properties.getProperty(key));
    }
}