package net.avdw.todo.config;

import java.util.Properties;

public interface APropertyRepository {
    Properties getProperties();

    void saveProperty(String key, String value);

    String getProperty(String key);
}
