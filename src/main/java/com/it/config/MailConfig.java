package com.it.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.MailSender;

public class MailConfig {
  private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);
  private static MailConfig instance = new MailConfig();

  private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
  private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
  private static final String MAIL_SMTP_HOST = "mail.smtp.host";
  private static final String MAIL_SMTP_PORT = "mail.smtp.port";
  private static final String MAIL_ID = "mail.id";
  private static final String MAIL_PASSWORD = "mail.password";

  private PropertiesConfiguration config;
  private String propertiesFile;
  private int masterPriority;
  private boolean starttlsEnable;
  private boolean smtpAuth;
  private String smtpHost;
  private String smtpPort;
  private String id;
  private String password;

  private boolean isEnable;

  private MailConfig() {}

  public static MailConfig getInstance() {
    return instance;
  }

  public void setPropertiesFile(String propertiesFile) {
    this.propertiesFile = propertiesFile;
  }

  public void init(String[] args) {
    try {
      config = new PropertiesConfiguration(propertiesFile);
      setStarttlsEnable(config.getBoolean(MAIL_SMTP_STARTTLS_ENABLE));
      setSmtpAuth(config.getBoolean(MAIL_SMTP_AUTH));
      setSmtpHost(config.getString(MAIL_SMTP_HOST));
      setSmtpPort(config.getString(MAIL_SMTP_PORT));
      setId(config.getString(MAIL_ID));
      setPassword(config.getString(MAIL_PASSWORD));
      MailSender.getInstance().init();
      isEnable = true;
    } catch (ConfigurationException e) {
      isEnable = false;
    }
    logger.info(" * MailConfig is {}", isEnable);
  }

  public boolean isStarttlsEnable() {
    return starttlsEnable;
  }

  public void setStarttlsEnable(boolean starttlsEnable) {
    this.starttlsEnable = starttlsEnable;
  }

  public boolean isSmtpAuth() {
    return smtpAuth;
  }

  public void setSmtpAuth(boolean smtpAuth) {
    this.smtpAuth = smtpAuth;
  }

  public String getSmtpHost() {
    return smtpHost;
  }

  public void setSmtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  public String getSmtpPort() {
    return smtpPort;
  }

  public void setSmtpPort(String smtpPort) {
    this.smtpPort = smtpPort;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isEnable() {
    return isEnable;
  }

  public void setEnable(boolean isEnable) {
    this.isEnable = isEnable;
  }
}
