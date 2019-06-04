package net.avdw.todo.property;

import java.util.Properties;

public interface APropertyRepository {
    Properties getProperties();

    void saveProperty(String key, String value);

    String getProperty(String key);
}
