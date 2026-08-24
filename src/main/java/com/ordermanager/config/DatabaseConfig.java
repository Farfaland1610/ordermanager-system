package com.ordermanager.config;

import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDbUrl() { return properties.getProperty("db.url"); }
    public static String getDbUser() { return properties.getProperty("db.user"); }
    public static String getDbPassword() { return properties.getProperty("db.password"); }
    public static String getPayflowHost() { return properties.getProperty("payflow.host", "127.0.0.1"); }
    public static int getPayflowPort() { return Integer.parseInt(properties.getProperty("payflow.port", "8080")); }
}