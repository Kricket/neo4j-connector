package com.netoprise.neo4j;

import java.io.IOException;
import java.util.Properties;

/**
 * Some unfortunatly useful methods
 * 
 * @author ndx
 * 
 */
public class ConnectorUtils {

	private static Properties properties;

	public static Properties getConnectorProperties() {
		if (properties == null) {
			synchronized(ConnectorUtils.class) {
				if(properties==null) {
					properties = new Properties();
					try {
						properties.load(ConnectorUtils.class.getResourceAsStream("/META-INF/connector.properties"));
					} catch (IOException e) {
						properties = null;
						throw new RuntimeException("failed at loading META-INF/connector.properties. That is really unfortunate, no ?", e);
					}
				}
			}
		}
		return properties;
	}

}
