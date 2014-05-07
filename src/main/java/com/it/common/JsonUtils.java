package com.it.common;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JsonUtils {
    private static final Logger logger = LoggerFactory
            .getLogger(JsonUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Gson GSON = new Gson();

    public static int getInt(String jsonString, String memberName) {
        if (jsonString == null || memberName == null) {
            throw new IllegalArgumentException();
        }
        if (jsonString.equals(StringUtils.EMPTY)
                || memberName.equals(StringUtils.EMPTY)) {
            throw new IllegalArgumentException();
        }

        JsonElement jelement = new JsonParser().parse(jsonString);
        if (jelement.isJsonPrimitive() || jelement.isJsonNull()) {
            throw new IllegalArgumentException();
        }

        JsonPrimitive jsonPrimitive = jelement.getAsJsonObject()
                .getAsJsonPrimitive(memberName);

        if (jsonPrimitive.isNumber()) {
            int value = jsonPrimitive.getAsInt();
            return value;
        } else {
            throw new NumberFormatException();
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

    public static <T> T fromJson(String content, Class<T> classOfT)
            throws IllegalArgumentException {
        return GSON.fromJson(content, classOfT);
    }

    public static void setup() {
    }
}
