package org.apache.tamaya.server;

import org.apache.tamaya.ConfigurationProvider;
import static org.apache.tamaya.functions.ConfigurationFunctions.*;
/**
 * Created by Anatole on 23.08.2015.
 */
public class ConfigService implements ConfigProviderService{
    @Override
    public String getConfiguration(String configId) {
        // currently ignore: with(section(configId,false))
        return ConfigurationProvider.getConfiguration().query(jsonInfo());
    }
}
