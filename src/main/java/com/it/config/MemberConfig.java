package com.it.config;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.it.model.AllMember;
import com.it.model.Member;

public class MemberConfig {
  private static final Logger logger = LoggerFactory.getLogger(MemberConfig.class);
  private static MemberConfig instance = new MemberConfig();

  private static final String CLUSTER = "cluster";
  private static final String HOST = "myinfo.host";
  private static final String PORT = "myinfo.port";
  private static final String DESC = "myinfo.desc";
  private static final String SPREAD_TIME = "config.spreadTime";
  private static final String MASTER_PRIORITY = "master.priority";
  private static final String MONITOR_ENABLE = "monitor.enable";
  private static final String MONITOR_PORT = "monitor.port";

  private PropertiesConfiguration config;
  private String propertiesFile;
  private String host;
  private int port;
  private String desc;

  private int spreadTime = 5;

  private boolean monitorEnable;
  private int monitorPort;

  private MemberConfig() {}

  public static MemberConfig getInstance() {
    return instance;
  }

  public void setPropertiesFile(String propertiesFile) {
    this.propertiesFile = propertiesFile;
  }

  public void init(String[] args) throws ConfigurationException {
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

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public int getSpreadTime() {
    return spreadTime;
  }

  public void setSpreadTime(int spreadTime) {
    this.spreadTime = spreadTime;
  }

  public boolean isAutoSpread() {
    return getSpreadTime() != 0;
  }

  public boolean isMonitorEnable() {
    return monitorEnable;
  }

  public void setMonitorEnable(boolean monitorEnable) {
    this.monitorEnable = monitorEnable;
  }

  public void setMonitorPort(int monitorPort) {
    this.monitorPort = monitorPort;
  }

  public int getMonitorPort() {
    return monitorPort;
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

  private void loadProperties() throws ConfigurationException {
    config = new PropertiesConfiguration(propertiesFile);
    config.setAutoSave(true);

    // from jopt
    setHost(JoptConfig.getInstance().getHost());
    setPort(JoptConfig.getInstance().getPort());
    setMonitorPort(JoptConfig.getInstance().getMonitorPort());

    if (host.equals(StringUtils.EMPTY)) {
      setHost(config.getString(HOST));
    }

    if (port == 0) {
      setPort(config.getInt(PORT));
    }

    setDesc(config.getString(DESC));

    // config
    setSpreadTime(config.getInt(SPREAD_TIME));

    // monitor
    setMonitorEnable(config.getBoolean(MONITOR_ENABLE));
    if (monitorPort == 0) {
      setMonitorPort(config.getInt(MONITOR_PORT));
    }

    // add cluster
    AllMember.getInstance().addClusters(getClustersArray());

    // add member
    for (String cluster : AllMember.getInstance().getClusters().getClusterNames()) {
      for (String hostPort : getMembers(cluster)) {
        String[] idHostPort = StringUtils.split(hostPort, ":");
        AllMember.getInstance().addMember(cluster,
            new Member(idHostPort[0], idHostPort[1], idHostPort[2], getHost(), getPort()));
      }
    }

    // set master.priority & bootup time & desc.
    Member me = AllMember.getInstance().me();
    me.setMasterPriority(config.getShort(MASTER_PRIORITY, (short) 0));
    me.setBootupTime(new Date());
    me.setDesc(desc);
  }
}
