import org.apache.tamaya.spi.PropertySource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Anatole on 20.03.2015.
 */
public class SimplePropertySource implements PropertySource{

    private Map<String,String> props = new HashMap<>();

    public SimplePropertySource() throws IOException {
        URL url = ClassLoader.getSystemClassLoader().getResource("META-INF/MyOtherConfigProperties.properties");
        Properties properties = new Properties();
        try(InputStream is = url.openStream()){
            properties.load(is);
        }
        finally{
            properties.forEach((k,v) -> props.put(k.toString(), v.toString()));
            props = Collections.unmodifiableMap(props);
        }
    }

    @Override
    public String getName() {
        return "META-INF/MyOtherConfigProperties.properties";
    }

    @Override
    public Map<String, String> getProperties() {
        return props;
    }
}
