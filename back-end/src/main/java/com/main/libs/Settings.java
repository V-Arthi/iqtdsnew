package com.main.libs;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {
	private static final Logger log = LogManager.getLogger(Settings.class);

	private static HashMap<PropertyType, Settings> properties;

	private Properties property;

	private Settings(Properties property) {
		this.property = property;
	}

	public static Settings getDBSettings() {
		return getSettings(PropertyType.DatabaseProperties);
	}

	public static Settings getUserSettings() {
		return getSettings(PropertyType.UserDefinedProperties);
	}

	public static Settings getDriverSettings() {
		return getSettings(PropertyType.DriverProperties);
	}

	public static Settings getTestManagementSettings() {
		return getSettings(PropertyType.TestManagementProperties);
	}

	public static Settings getFileNameSettings(String fileType) {
		return getSettings(fileType.contentEquals("837I") ? PropertyType.FileNameProperty_837I
				: PropertyType.FileNameProperty_837P);
	}

	private static Settings getSettings(PropertyType type) {
		properties = properties == null ? new HashMap<PropertyType, Settings>() : properties;
		if (properties.containsKey(type))
			return properties.get(type);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		Properties property = new Properties();

		try (InputStream resourceStream = loader.getResourceAsStream(type.getValue())) {
			property.load(resourceStream);
		} catch (IOException e1) {
			log.error("Error while reading property file using stream :" + type.getValue());
		}

		return new Settings(property);
	}

	public String getProperty(String key) {
		return property.getProperty(key, null);
	}

	public String getDBproperty(String env, String db, String propertyName) {
		return property.getProperty(String.format("%s.%s.%s", env, db, propertyName));
	}

	public static Properties getApplicationProperties() throws Exception {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties property = new Properties();
		InputStream resourceStream = loader.getResourceAsStream("application.properties");
		property.load(resourceStream);

		return property;

	}
}
