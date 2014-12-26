package org.apache.tamaya.core.internal.config;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.tamaya.Configuration;

/**
 * Implementation of Configuration which the information is from xml or properties files.
 * Once the File modified, it will update automatically by provider.
 * @see FilesPropertiesConfigProvider
 * @see FileChangeObserver
 * @author otaviojava
 */
class FileConfiguration implements Configuration, FileChangeObserver {

	private Map<String, String> configurationMap;

	public FileConfiguration(Map<String, String> configurationMap) {
        this.configurationMap = configurationMap;
    }

    @Override
	public Optional<String> get(String key) {
		return Optional.ofNullable(configurationMap.get(key));
	}

    @Override
	public String getName() {
		return "files.config";
	}

	@Override
	public Map<String, String> getProperties() {
		return configurationMap;
	}

    @Override
    public void update(Map<String, String> configurationMap) {
        synchronized (this) {
            this.configurationMap = configurationMap;
        }
    }

    @Override
    public String toString() {
        return "org.apache.tamaya.core.internal.config.FileConfiguration: " + configurationMap.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(configurationMap);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(Configuration.class.isInstance(obj)) {
            Configuration other = Configuration.class.cast(obj);
            return Objects.equals(configurationMap, other.getProperties());
        }

        return false;
    }
}
