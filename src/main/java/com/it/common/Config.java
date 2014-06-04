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

import com.google.common.base.Joiner;
import com.it.model.AllMember;
import com.it.model.Member;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static Config instance = new Config();

    private static final String DEFAULT_PROPERTIES_NAME = "server.properties";
    private static final String CLUSTER = "cluster";
    private static final String HOST = "myinfo.host";
    private static final String PORT = "myinfo.port";
    private static final String AUTO_SPREAD = "config.autoSpread";

    private OptionParser parser = new OptionParser();
    private ArgumentAcceptingOptionSpec<String> propertiesOpt = parser
            .accepts("prop", "properties file").withOptionalArg()
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
    private int spreadTime = 5;
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

        logger.info(" * Config");
        logger.info(AllMember.getInstance().toString());
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
    
    public int getSpreadTime() {
        return spreadTime;
    }
    
    public void setSpreadTime(int spreadTime) {
        this.spreadTime = spreadTime;
    }

    public boolean isAutoSpread() {
        return isAutoSpread;
    }

    public void setAutoSpread(boolean isAutoSpread) {
        this.isAutoSpread = isAutoSpread;
    }

    public void addMember(String cluster, Member member) {
        if (!ArrayUtils.contains(getClustersArray(), cluster)) {
            config.addProperty(CLUSTER, cluster);
        }

        config.addProperty(getSubCluster(cluster), member.getHostPort());
    }

    public void removeCluster(String removedCluster) {
        List<String> clusters = getClustersList();

        for (int i = 0; i < clusters.size(); i++) {
            if (StringUtils.equals(clusters.get(i), removedCluster)) {
                clusters.remove(i);
            }
        }

        config.setProperty(CLUSTER, Joiner.on(",").join(clusters));
    }

    public void removeMember(String cluster, Member member) {
        List<String> hostPorts = getMembers(cluster);
        config.clearProperty(getSubCluster(cluster));
        String removedHostPort = member.getHostPort();
        for (String hostPort : hostPorts) {
            if (StringUtils.equals(removedHostPort, hostPort)) {
                hostPorts.remove(hostPort);
                break;
            }
        }

        for (String hostPort : hostPorts) {
            config.addProperty(getSubCluster(cluster), hostPort);
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getMembers(String cluster) {
        return config.getList(getSubCluster(cluster));
    }

    private String[] getClustersArray() {
        return config.getStringArray(CLUSTER);
    }

    @SuppressWarnings("unchecked")
    private List<String> getClustersList() {
        return config.getList(CLUSTER);
    }

    private String getSubCluster(String cluster) {
        return CLUSTER + "." + cluster;
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

        // add cluster
        AllMember.getInstance().addCluster(getClustersArray());

        // add member
        for (String cluster : AllMember.getInstance().getClusters()
                .getClusterNames()) {
            for (String hostPort : getMembers(cluster)) {
                String[] splitedHostPort = StringUtils.split(hostPort, ":");
                AllMember.getInstance().addMember(
                        cluster,
                        new Member(splitedHostPort[0], splitedHostPort[1],
                                getHost(), getPort()));
            }
        }
    }

    private void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }
}