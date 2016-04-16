package org.apache.tamaya.ui.internal;

import org.apache.tamaya.spisupport.BasePropertySource;
import org.apache.tamaya.spisupport.MapPropertySource;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple property source, used for internationalization.
 */
final class URLPropertySource extends BasePropertySource{

    private static final Logger LOG = Logger.getLogger(URLPropertySource.class.getName());
    private URL url;
    private Map<String, String> properties;

    public URLPropertySource(URL url){
        this.url = Objects.requireNonNull(url);
        load();
    }

    /**
     * Loads/reloads the properties from the URL. If loading of the properties failed the previus state is preserved,
     * unless there is no such state. In this case an empty map is assigned.
     */
    public void load(){
        try(InputStream is = url.openStream()) {
            Properties props = new Properties();
            if (url.getFile().endsWith(".xml")) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
            properties = Collections.unmodifiableMap(MapPropertySource.getMap(props));
        }
        catch(Exception e){
            LOG.log(Level.WARNING, "Failed to read config from "+url,e);
            if(properties==null) {
                properties = Collections.emptyMap();
            }
        }
    }

    @Override
    public String getName() {
        return url.toString();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
