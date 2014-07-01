package com.it.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.it.monitor.MonitorServerHandler;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static boolean isIpValid(String ip) {
    return InetAddressValidator.getInstance().isValid(ip);
  }

  public static boolean isPortValid(Integer port) {
    return IntegerValidator.getInstance().isInRange(port, 1, 65535);
  }

  public static boolean isPortValid(String port) {
    boolean valid = false;
    try {
      valid = IntegerValidator.getInstance().isInRange(parseInt(port), 1, 65535);
    } catch (NumberFormatException e) {
      return valid;
    }
    return valid;
  }

  public static int parseInt(String s) {
    return parseInt(s, 0);
  }

  public static int parseInt(String s, int defaultValue) {
    if (s == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(s, 10);
    } catch (NumberFormatException x) {
      return defaultValue;
    }
  }

  public static String toJson(Object obj) {
		if (obj == null) {
			return new JsonObject().toString();
		}
		try {
			return MAPPER.writeValueAsString(obj);
		} catch (IOException e) {
			return new JsonObject().toString();
		}
  }

  public static <T> T fromJson(String content, Class<T> classOfT) {
    T result = null;
    try {
      result = MAPPER.readValue(content, classOfT);
    } catch (IOException e) {
			e.printStackTrace();
    }
    return result;
  }
}
