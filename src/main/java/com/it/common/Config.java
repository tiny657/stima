package com.it.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.it.model.AllServer;
import com.it.model.Categories;
import com.it.model.Server;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static Config instance = new Config();

    private static final String DEFAULT_PROPERTIES_NAME = "server.properties";
    private static final String CATEGORY = "category";
    private static final String HOST = "myinfo.host";
    private static final String PORT = "myinfo.port";
    private static final String AUTO_SPREAD = "config.autoSpread";

    private OptionParser parser = new OptionParser();
    private ArgumentAcceptingOptionSpec<String> propertiesOpt = parser
            .accepts("properties", "properties file").withOptionalArg()
            .ofType(String.class).defaultsTo(DEFAULT_PROPERTIES_NAME);
    private ArgumentAcceptingOptionSpec<String> hostOpt = parser
            .accepts("host", "this server's host").withOptionalArg()
            .ofType(String.class).defaultsTo(StringUtils.EMPTY);
    private ArgumentAcceptingOptionSpec<Integer> portOpt = parser
            .accepts("port", "this server's port").withOptionalArg()
            .ofType(Integer.class).defaultsTo(0);

    private PropertiesConfiguration config;
    private String propertiesFile;
    private String host;
    private int port;
    private boolean isAutoSpread;

    private Config() {
    }

    public static Config getInstance() {
        return instance;
    }

    public void init(String[] args) throws FileNotFoundException, IOException,
            Exception {
        loadJoptOptions(args);
        loadProperties();

        if (!validate()) {
            throw new Exception(host + ":" + port + " isn't valid.");
        }

        logger.info(AllServer.getInstance().toString());
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

    public boolean isAutoSpread() {
        return isAutoSpread;
    }

    public void setAutoSpread(boolean isAutoSpread) {
        this.isAutoSpread = isAutoSpread;
    }

    public void addServer(String category, Server server) {
        if (!ArrayUtils.contains(getCategories(), category)) {
            config.addProperty(CATEGORY, category);
        }

        config.addProperty(getSubCategory(category), server.getHost() + ":"
                + server.getPort());
    }

    @SuppressWarnings("unchecked")
    public List<String> getServer(String category) {
        return config.getList(getSubCategory(category));
    }

    public void removeServer(String category, Server server) {
        // TODO :: remove category in properties file.
        List<String> hostPorts = getServer(category);
        config.clearProperty(getSubCategory(category));
        String removedHostPort = server.getHost() + ":" + server.getPort();
        for (String hostPort : hostPorts) {
            if (StringUtils.equals(removedHostPort, hostPort)) {
                hostPorts.remove(hostPort);
                break;
            }
        }

        for (String hostPort : hostPorts) {
            config.addProperty(getSubCategory(category), hostPort);
        }
    }

    public String[] getCategories() {
        return config.getStringArray(CATEGORY);
    }

    public List<Server> getServers() {
        List<Server> servers = Lists.newArrayList();
        Categories categories = AllServer.getInstance().getCategories();
        for (String categoryName : categories.getCategoryNames()) {
            for (Server server : categories.getServerListIn(categoryName)
                    .getServers()) {
                servers.add(server);
            }
        }
        return servers;
    }

    private String getSubCategory(String category) {
        return CATEGORY + "." + category;
    }

    private boolean validate() {
        for (Server server : getServers()) {
            if (server.getHost().equals(host) && server.getPort() == port) {
                return true;
            }
        }

        return false;
    }

    private void loadJoptOptions(String[] args) {
        OptionSet options = parser.parse(args);
        setPropertiesFile(options.valueOf(propertiesOpt));
        setHost(options.valueOf(hostOpt));
        setPort(options.valueOf(portOpt));
    }

    private void loadProperties() throws ConfigurationException {
        config = new PropertiesConfiguration(propertiesFile);
        config.setAutoSave(true);

        if (host.equals(StringUtils.EMPTY)) {
            setHost(config.getString(HOST));
        }

        if (port == 0) {
            setPort(config.getInt(PORT));
        }

        setAutoSpread(config.getBoolean(AUTO_SPREAD));

        // add category
        AllServer.getInstance().addCategory(getCategories());

        // add server
        for (String category : AllServer.getInstance().getCategories()
                .getCategoryNames()) {
            for (String hostPort : getServer(category)) {
                String[] splitedHostPort = StringUtils.split(hostPort, ":");
                AllServer.getInstance().addServer(category,
                        new Server(splitedHostPort[0], splitedHostPort[1]));
            }
        }
    }

    private void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }
}