import org.apache.tamaya.core.propertysource.SimplePropertiesPropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anatole on 20.03.2015.
 */
public class SimplePropertySourceProvider implements PropertySourceProvider {

    //    @Override
//    public Collection<PropertySource> getPropertySources() {
//        return null;
//    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        List<PropertySource> propertySources = new ArrayList<>();
        String[] resources = new String[]{
                "cfgOther/a.properties", "cfgOther/b.properties", "cfgOther/c.properties"};
        for (String res : resources) {
            URL url = ClassLoader.getSystemClassLoader().getResource(res);
            propertySources.add(new SimplePropertiesPropertySource(url));
        }
        return Collections.unmodifiableList(propertySources);
    }

}
