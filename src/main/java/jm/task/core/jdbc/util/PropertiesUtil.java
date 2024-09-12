package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            PROPERTIES.load(inputStream);

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
