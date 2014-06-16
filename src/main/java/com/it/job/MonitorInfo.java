package com.it.job;

import javax.management.openmbean.CompositeData;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public abstract class MonitorInfo {
    public abstract void parse(CompositeData cd);

    protected static Object getObject(CompositeData cd, String itemName) {
        return cd.get(itemName);
    }

    protected static String getString(CompositeData cd, String itemName) {
        return (String) getObject(cd, itemName);
    }

    protected static long getLong(CompositeData cd, String itemName) {
        return (Long) getObject(cd, itemName);
    }

    protected static int getInt(CompositeData cd, String itemName) {
        return (Integer) getObject(cd, itemName);
    }

    protected static float getFloat(CompositeData cd, String itemName) {
        return (Float) getObject(cd, itemName);
    }

    protected static boolean containsKey(CompositeData cd, String itemName) {
        return cd.containsKey(itemName);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

}
