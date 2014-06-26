package com.it.common;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.IntegerValidator;

public class Utils {
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
    if (null == s)
      return defaultValue;
    try {
      return Integer.parseInt(s, 10);
    } catch (NumberFormatException x) {
      return defaultValue;
    }
  }
}
