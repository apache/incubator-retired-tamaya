package org.apache.tamaya.core.internal.config;

import java.util.Map;

/**
 * Observer to be used in {@link FileChangeListener} to commit all configurations and provider.
 * @author otaviojava
 */
interface FileChangeObserver {

    void update(Map<String, String> configurationMap);

}
