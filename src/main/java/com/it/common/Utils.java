package com.it.common;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

public class Utils {
  private static final Logger logger = LoggerFactory.getLogger(Utils.class);

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static boolean isMemberValid(String idHostPort) {
    String[] split = StringUtils.split(idHostPort, ":");
    if (split.length != 3) {
      logger.error("The member must be [ID]:[IP]:[port]");
      return false;
    }

    if (parseInt(split[0], -1) == -1) {
      logger.error("The ID({}) of the member must be an integer.", split[0]);
      return false;
    }

    if (!isIpValid(split[1])) {
      logger.error("The IP({}) of the member is invalid.", split[1]);
      return false;
    }

    if (!isPortValid(split[2])) {
      logger.error("The port({}) range is 0-65535.", split[2]);
      return false;
    }

    return true;
  }

  public static boolean isIpValid(String ip) {
    return InetAddressValidator.getInstance().isValid(ip);
  }

  public static boolean isPortValid(String port) {
    boolean valid = false;
    try {
      valid = isPortValid(parseInt(port));
    } catch (NumberFormatException e) {
      return valid;
    }
    return valid;
  }

  public static boolean isPortValid(Integer port) {
    return IntegerValidator.getInstance().isInRange(port, 1, 65535);
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
