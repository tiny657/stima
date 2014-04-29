///*
// * @(#) LSConfig.java Jul 1, 2013 
// *
// * Copyright 2013 NHN Corp. All rights Reserved. 
// * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
// */
//package com.it.perf;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.nhncorp.npush.nni.lookup.lb.RoundRobin;
//import com.nhncorp.npush.nni.lookup.lb.WeightConnection;
//import com.nhncorp.npush.nni.lookup.util.JsonUtils;
//
//public class LSConfig {
//	private static final Logger LOG = LoggerFactory.getLogger(LSConfig.class);
//	private static final Integer DEFAULT_CLIENT_READTIMEOUT = 60;
//	private static final Integer DEFAULT_MONITOR_PORT = 11000;
//	private static final long DEFAULT_NIMM_INVOKE_TIMEOUT = 700L;
//
//	private static LSConfig serverConfig = new LSConfig();
//	private String service;
//	private String cryptoKeyValue;
//	private boolean isEncryptionEnable;
//	private String nimmConfigFile;
//	private Integer nimmDomainId; // LookupServer Nimm DomainID
//	private Integer smsNimmDomainId; // ServerManagerServer Nimm DomainID
//
//	private long nimmInvokeTimeout;
//
//	private Integer serverInfoPort;
//	private Integer serverInfoService;
//	private Integer killerPort;
//	private Integer nimmServerID;
//
//	private String propertiesFileName;
//
//	private Integer clientReadtimeout;
//	private Integer monitorPort;
//
//	private Class<?> defaultLBPolicy;
//	private Class<?> currentLBPolicy;
//
//	/**
//	 * key region, value serverList
//	 */
//	private Map<String, Object> lbServerList;
//	private Map<String, Object> lbRegionInfo;
//
//	private boolean isLocalServerConfigUse;
//
//	private LSConfig() {
//		this.defaultLBPolicy = RoundRobin.class; // default		
//		this.lbRegionInfo = new HashMap<String, Object>();
//		this.lbServerList = new HashMap<String, Object>();
//	}
//
//	public static LSConfig getInstance() {
//		return serverConfig;
//	}
//
//	/**
//	 * ServerConfig initial
//	 * @throws IOException
//	 * @throws FileNotFoundException
//	 */
//	@SuppressWarnings("unchecked")
//	public void loadProperties() throws IOException, FileNotFoundException {
//
//		if (getPropertiesFileName() == null) {
//			setPropertiesFileName(Consts.DEFAULT_PROPERTIES_NAME);
//		}
//
//		// properties 파일을 읽는다.		
//		Properties properties = new Properties();
//
//		FileInputStream fis = null;
//		try {
//			fis = new FileInputStream(getClass().getResource(getPropertiesFileName()).getPath());
//			properties.load(fis);
//		} catch (FileNotFoundException e) {
//			LOG.error("PROPERTIES 파일이 존재하지 않습니다. FileName : " + getPropertiesFileName());
//			throw e;
//		} catch (IOException e) {
//			LOG.error("초기화 중 오류발생", e);
//			throw e;
//		} finally {
//			if (fis != null) {
//				IOUtils.closeQuietly(fis);
//			}
//		}
//
//		// setting 
//		setService(getStringValue(properties, Consts.CONFIG_KEY_SERVICE));
//		setCryptoKeyValue(getStringValue(properties, Consts.CONFIG_KEY_CRYPTO_KEY_VALUE));
//		setEncryptionEnable(getBooleanValue(properties, Consts.CONFIG_KEY_ENCRYPTION_ENABLE, true));
//		setNimmConfigFile(getStringValue(properties, Consts.CONFIG_KEY_NIMM_CONFIGFILE));
//		setNimmDomainId(getIntegerValue(properties, Consts.CONFIG_KEY_NIMM_DOMAINID));
//		setSmsNimmDomainId(getIntegerValue(properties, Consts.CONFIG_KEY_SMS_NIMM_DOMAINID));
//		setNimmInvokeTimeout(getLongValue(properties, Consts.CONFIG_KEY_NIMM_INVOKE_TIMEOUT, DEFAULT_NIMM_INVOKE_TIMEOUT));
//
//		setServerInfoPort(getIntegerValue(properties, Consts.CONFIG_KEY_SERVERINFO_PORT));
//		setServerInfoService(getIntegerValue(properties, Consts.CONFIG_KEY_SERVERINFO_SERVICE));
//		setKillerPort(getIntegerValue(properties, Consts.CONFIG_KEY_KILLER_PORT));
//		setMonitorPort(getIntegerValue(properties, Consts.CONFIG_KEY_TELNET_PORT, DEFAULT_MONITOR_PORT));
//		setClientReadtimeout(getIntegerValue(properties, Consts.CONFIG_KEY_CLIENT_READTIMEOUT, DEFAULT_CLIENT_READTIMEOUT));
//
//		setDefaultLBPolicy(getLBPolicyValue(properties, Consts.CONFIG_KEY_DEFAULT_LBPOLICY, RoundRobin.class));
//		setCurrentLBPolicy(this.defaultLBPolicy);
//		setLbServerList(JsonUtils.fromJson(getStringValue(properties, Consts.CONFIG_KEY_LB_SERVERLIST, ""), Map.class));
//		setLbRegionInfo(JsonUtils.fromJson(getStringValue(properties, Consts.CONFIG_KEY_LB_REGIONLIST, ""), Map.class));
//		setLocalConfigListUse(getBooleanValue(properties, Consts.CONFIG_KEY_LOCALCONFIG_USE, false));
//
//	}
//
//	private Class<?> getLBPolicyValue(Properties properties, String key, Class<?> defaultValue) {
//		String lbValue = getStringValue(properties, key, Consts.LB_POLICY_ROUNDROBIN);
//		if (StringUtils.equals(lbValue, Consts.LB_POLICY_ROUNDROBIN)) {
//			return RoundRobin.class;
//		} else if (StringUtils.equals(lbValue, Consts.LB_POLICY_WEIGHTCONNECTION)) {
//			return WeightConnection.class;
//		} else {
//			return defaultValue;
//		}
//	}
//
//	private String getStringValue(Properties properties, String key, String defaultValue) {
//		try {
//			return properties.getProperty(key);
//		} catch (Exception e) {
//			return defaultValue;
//		}
//	}
//
//	private String getStringValue(Properties properties, String key) {
//		if (properties == null) {
//			throw new IllegalArgumentException("properties is null");
//		}
//		return properties.getProperty(key);
//	}
//
//	private Boolean getBooleanValue(Properties properties, String key) {
//		if (properties == null) {
//			throw new IllegalArgumentException("properties is null");
//		}
//		return Boolean.valueOf(properties.getProperty(key));
//	}
//
//	private Boolean getBooleanValue(Properties properties, String key, boolean defaultValue) {
//		try {
//			return getBooleanValue(properties, key);
//		} catch (Exception e) {
//			return defaultValue;
//		}
//	}
//
//	private Integer getIntegerValue(Properties properties, String key, int defaultValue) {
//		try {
//			Integer intValue = getIntegerValue(properties, key);
//			return intValue;
//		} catch (Exception e) {
//			return defaultValue;
//		}
//	}
//
//	private Integer getIntegerValue(Properties properties, String key) {
//		if (properties == null) {
//			throw new IllegalArgumentException("properties is null");
//		}
//		return Integer.valueOf(properties.getProperty(key));
//	}
//
//	private Long getLongValue(Properties properties, String key, Long defaultValue) {
//		try {
//			Long longValue = getLongValue(properties, key);
//			return longValue;
//		} catch (Exception e) {
//			return defaultValue;
//		}
//	}
//
//	private Long getLongValue(Properties properties, String key) {
//		if (properties == null) {
//			throw new IllegalArgumentException("properties is null");
//		}
//		return Long.valueOf(properties.getProperty(key));
//	}
//
//	public String getService() {
//		return service;
//	}
//
//	public void setService(String service) {
//		this.service = service;
//	}
//
//	public String getCryptoKeyValue() {
//		return cryptoKeyValue;
//	}
//
//	private void setCryptoKeyValue(String cryptoKeyValue) {
//		this.cryptoKeyValue = cryptoKeyValue;
//	}
//
//	public String getNimmConfigFile() {
//		return nimmConfigFile;
//	}
//
//	private void setNimmConfigFile(String nimmConfigFile) {
//		this.nimmConfigFile = nimmConfigFile;
//	}
//
//	public Integer getNimmDomainId() {
//		return nimmDomainId;
//	}
//
//	private void setNimmDomainId(Integer nimmDomainId) {
//		this.nimmDomainId = nimmDomainId;
//	}
//
//	public Integer getServerInfoPort() {
//		return serverInfoPort;
//	}
//
//	private void setServerInfoPort(Integer serverInfoPort) {
//		this.serverInfoPort = serverInfoPort;
//	}
//
//	public Integer getServerInfoService() {
//		return serverInfoService;
//	}
//
//	private void setServerInfoService(Integer serverInfoService) {
//		this.serverInfoService = serverInfoService;
//	}
//
//	public Integer getKillerPort() {
//		return killerPort;
//	}
//
//	private void setKillerPort(Integer killerPort) {
//		this.killerPort = killerPort;
//	}
//
//	public Integer getNimmServerID() {
//		return nimmServerID;
//	}
//
//	public void setNimmServerID(Integer nimmServerID) {
//		this.nimmServerID = nimmServerID;
//	}
//
//	public String getPropertiesFileName() {
//		return propertiesFileName;
//	}
//
//	public void setPropertiesFileName(String fileName) {
//		propertiesFileName = fileName;
//	}
//
//	public Integer getClientReadtimeout() {
//		return clientReadtimeout;
//	}
//
//	public void setClientReadtimeout(Integer clientReadtimeout) {
//		this.clientReadtimeout = clientReadtimeout;
//	}
//
//	public Integer getMonitorPort() {
//		return monitorPort;
//	}
//
//	public void setMonitorPort(Integer monitorPort) {
//		this.monitorPort = monitorPort;
//	}
//
//	public Integer getSmsNimmDomainId() {
//		return smsNimmDomainId;
//	}
//
//	public void setSmsNimmDomainId(Integer smsNimmDomainId) {
//		this.smsNimmDomainId = smsNimmDomainId;
//	}
//
//	public Class<?> getDefaultLBPolicy() {
//		return defaultLBPolicy;
//	}
//
//	private void setDefaultLBPolicy(Class<?> defaultLBPolicy) {
//		this.defaultLBPolicy = defaultLBPolicy;
//	}
//
//	public Class<?> getCurrentLBPolicy() {
//		return currentLBPolicy;
//	}
//
//	public void setCurrentLBPolicy(Class<?> currentLBPolicy) {
//		this.currentLBPolicy = currentLBPolicy;
//	}
//
//	public Map<String, Object> getLbServerList() {
//		return lbServerList;
//	}
//
//	public boolean isEncryptionEnable() {
//		return isEncryptionEnable;
//	}
//
//	public void setEncryptionEnable(boolean isEncryptionEnable) {
//		this.isEncryptionEnable = isEncryptionEnable;
//	}
//
//	public void setLbServerList(Map<String, Object> lbServerList) {
//		this.lbServerList.clear();
//		this.lbServerList.putAll(lbServerList);
//	}
//
//	public boolean isLocalServerConfigUse() {
//		return this.isLocalServerConfigUse;
//	}
//
//	public void setLocalConfigListUse(boolean localServerListUse) {
//		this.isLocalServerConfigUse = localServerListUse;
//	}
//
//	public Map<String, Object> getLbRegionInfo() {
//		return this.lbRegionInfo;
//	}
//
//	public void setLbRegionInfo(Map<String, Object> lbRegionInfo) {
//		this.lbRegionInfo.clear();
//		this.lbRegionInfo.putAll(lbRegionInfo);
//	}
//
//	public void setNimmInvokeTimeout(long nimmInvokeTimeout) {
//		this.nimmInvokeTimeout = nimmInvokeTimeout;
//	}
//
//	public long getNimmInvokeTimeout() {
//		return this.nimmInvokeTimeout;
//	}
//}
