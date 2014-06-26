package com.it.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoptConfig {
  private static final Logger logger = LoggerFactory.getLogger(JoptConfig.class);
  private static JoptConfig instance = new JoptConfig();

  private static final String DEFAULT_MEMBER_PROPERTIES = "member.properties";
  private static final String DEFAULT_MAIL_PROPERTIES = "mail.properties";

  private OptionParser parser = new OptionParser();
  private ArgumentAcceptingOptionSpec<String> memberPropertiesOpt = parser
      .accepts("memberProp", "member properties file").withOptionalArg().ofType(String.class)
      .defaultsTo(DEFAULT_MEMBER_PROPERTIES);
  private ArgumentAcceptingOptionSpec<String> mailPropertiesOpt = parser
      .accepts("mailProp", "mail properties file").withOptionalArg().ofType(String.class)
      .defaultsTo(DEFAULT_MAIL_PROPERTIES);
  private ArgumentAcceptingOptionSpec<String> hostOpt = parser
      .accepts("host", "this server's host").withOptionalArg().ofType(String.class)
      .defaultsTo(StringUtils.EMPTY);
  private ArgumentAcceptingOptionSpec<Integer> portOpt = parser
      .accepts("port", "this server's port").withOptionalArg().ofType(Integer.class).defaultsTo(0);
  private OptionSpecBuilder senderOpt = parser.accepts("sender", "If set, this is the sender.");

  private String host;
  private int port;
  private boolean isSender;

  private JoptConfig() {}

  public static JoptConfig getInstance() {
    return instance;
  }

  public void init(String[] args) throws FileNotFoundException, IOException, Exception {
    loadJoptOptions(args);
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

  public boolean isSender() {
    return isSender;
  }

  public void setSender(boolean isSender) {
    this.isSender = isSender;
  }

  private void loadJoptOptions(String[] args) {
    OptionSet options = parser.parse(args);
    MemberConfig.getInstance().setPropertiesFile(options.valueOf(memberPropertiesOpt));
    MailConfig.getInstance().setPropertiesFile(options.valueOf(mailPropertiesOpt));
    setHost(options.valueOf(hostOpt));
    setPort(options.valueOf(portOpt));
    setSender(options.has(senderOpt));
  }
}
