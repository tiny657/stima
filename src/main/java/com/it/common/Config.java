package com.it.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.model.AllServer;
import com.it.model.Server;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String DEFAULT_PROPERTIES_NAME = "server.properties";
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

        logger.info(AllServer.getInstance().toString());
    }

    public void loadProperties() throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration(
                DEFAULT_PROPERTIES_NAME);
        config.setAutoSave(true);

        if (host.equals(StringUtils.EMPTY)) {
            setHost(config.getString(HOST));
        }

        if (port == 0) {
            setPort(config.getInt(PORT));
        }

        // add category
        AllServer.getInstance().addCategory(config.getStringArray(CATEGORY));

        // add server
        for (String category : AllServer.getInstance().getCategories().keySet()) {
            String[] hostPorts = config.getStringArray(CATEGORY + "."
                    + category);
            for (String hostPort : hostPorts) {
                String[] splitedHostPort = StringUtils.split(hostPort, ":");
                AllServer.getInstance().addServer(category,
                        new Server(splitedHostPort[0], splitedHostPort[1]));
            }
        }
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
}