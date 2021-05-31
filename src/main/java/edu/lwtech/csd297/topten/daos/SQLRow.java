package edu.lwtech.csd297.topten.daos;

import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.*;

class SQLRow {
    private static final Logger logger = LogManager.getLogger(SQLRow.class.getName());

    private static final String VALUE_KEY = "Value";

    private Map<String,String> items;

    public SQLRow() {
        items = new HashMap<>();
    }

    public SQLRow(String name, String value) {
        items = new HashMap<>();
        items.put(name, value);
    }

    public SQLRow(String name, int value) {
        this(name, ""+value);
    }

    public SQLRow(String value) {
        this(VALUE_KEY, value);
    }

    public SQLRow(int value) {
        this(VALUE_KEY, ""+value);
    }

    public SQLRow(List<String> columns, ResultSet rs) {
        items = new HashMap<>();

        try {
            for (int i=0; i < columns.size(); i++) {
                items.put(columns.get(i), rs.getString(i+1));
            }
        } catch (SQLException e) {
            logger.error("Unable to construct SQLRow object from ResultSet.", e);
        }
    }

    public String getItem(String name) {
        if (name == null) throw new IllegalArgumentException("name cannot be passed to getItem()");
        return items.get(name);
    }

    public String getItem() {
        return getItem(VALUE_KEY);
    }

    public void addItem(String name, String value) {
        if (name == null || value == null) throw new IllegalArgumentException("Null parameter passed to addItem()");
        items.put(name, value);
    }

    public void removeItem(String name) {
        if (name == null) throw new IllegalArgumentException("Null parameter passed to removeItem()");
        items.remove(name);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("{");
        String comma = "";
        for (Map.Entry<String, String> entry : items.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            s.append(comma).append("{").append(name).append(",").append(value).append("}");
            comma = ",";
        }
        s.append("}");
        return s.toString();
    }
    
}