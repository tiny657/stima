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
import com.google.common.collect.Lists;
import com.it.model.AllMember;
import com.it.model.Categories;
import com.it.model.Member;

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
            logger.error("My server info is {}:{}\n", host, port);
            logger.error(AllMember.getInstance().getCategories().toString());
            throw new Exception(host + ":" + port + " isn't valid.");
        }

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

    public boolean isAutoSpread() {
        return isAutoSpread;
    }

    public void setAutoSpread(boolean isAutoSpread) {
        this.isAutoSpread = isAutoSpread;
    }

    public void addMember(String category, Member member) {
        if (!ArrayUtils.contains(getCategoriesArray(), category)) {
            config.addProperty(CATEGORY, category);
        }

        config.addProperty(getSubCategory(category), member.getHostPort());
    }

    @SuppressWarnings("unchecked")
    public List<String> getMember(String category) {
        return config.getList(getSubCategory(category));
    }

    public void removeCategory(String removedCategory) {
        List<String> categories = getCategoriesList();

        for (int i = 0; i < categories.size(); i++) {
            if (StringUtils.equals(categories.get(i), removedCategory)) {
                categories.remove(i);
            }
        }

        config.setProperty(CATEGORY, Joiner.on(",").join(categories));
    }

    public void removeMember(String category, Member member) {
        List<String> hostPorts = getMember(category);
        config.clearProperty(getSubCategory(category));
        String removedHostPort = member.getHostPort();
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

    public String[] getCategoriesArray() {
        return config.getStringArray(CATEGORY);
    }

    @SuppressWarnings("unchecked")
    public List<String> getCategoriesList() {
        return config.getList(CATEGORY);
    }

    public List<Member> getMembers() {
        List<Member> members = Lists.newArrayList();
        Categories categories = AllMember.getInstance().getCategories();
        for (String categoryName : categories.getCategoryNames()) {
            for (Member member : categories.getMemberListIn(categoryName)
                    .getMembers()) {
                members.add(member);
            }
        }
        return members;
    }

    private String getSubCategory(String category) {
        return CATEGORY + "." + category;
    }

    private boolean validate() {
        for (Member member : getMembers()) {
            if (member.getHost().equals(host) && member.getPort() == port) {
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
        AllMember.getInstance().addCategory(getCategoriesArray());

        // add member
        for (String category : AllMember.getInstance().getCategories()
                .getCategoryNames()) {
            for (String hostPort : getMember(category)) {
                String[] splitedHostPort = StringUtils.split(hostPort, ":");
                AllMember.getInstance().addMember(category,
                        new Member(splitedHostPort[0], splitedHostPort[1], getHost(), getPort()));
            }
        }
    }

    private void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }
}