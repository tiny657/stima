/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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

  public static boolean isValidMember(String idHostPort) {
    String[] split = StringUtils.split(idHostPort, ":");
    if (split.length != 4) {
      logger.error("The member must be [ID]:[IP]:[dataPort]:[controlPort]");
      return false;
    }

    if (parseInt(split[0], -1) == -1) {
      logger.error("The ID({}) of the member must be an integer.", split[0]);
      return false;
    }

    if (!isValidIP(split[1])) {
      logger.error("The IP({}) of the member is invalid.", split[1]);
      return false;
    }

    if (!isValidPort(split[2])) {
      logger.error("The dataPort({}) range is 1-65535.", split[2]);
      return false;
    }

    if (!isValidPort(split[3])) {
      logger.error("The controlPort({}) range is 1-65535.", split[3]);
      return false;
    }

    return true;
  }

  public static boolean isValidIP(String ip) {
    return InetAddressValidator.getInstance().isValid(ip);
  }

  public static boolean isValidPort(String port) {
    boolean valid = false;
    try {
      valid = isValidPort(parseInt(port));
    } catch (NumberFormatException e) {
      return valid;
    }
    return valid;
  }

  public static boolean isValidPort(Integer port) {
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
