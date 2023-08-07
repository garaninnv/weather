package com.garanin.weather.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertProject {
    private static Properties properties = new Properties();
    private static PropertProject props = new PropertProject();
    public static PropertProject getProps(){
        if (props == null) {
            props = new PropertProject();
        }
        return props;
    }

    public PropertProject() {
    }

    public String getKey(String key) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }
}