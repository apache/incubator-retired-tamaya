import org.apache.tamaya.core.propertysource.SimplePropertiesPropertySource;
import org.apache.tamaya.resource.AbstractPathPropertySourceProvider;
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
public class SimplePropertySourceProvider2 extends AbstractPathPropertySourceProvider {

    public SimplePropertySourceProvider2(){
        super("cfgOther/**/*.properties", "META-INF/MyOtherConfigProperties.*");
    }

    @Override
    protected PropertySource getPropertySource(URL url) {
        return new SimplePropertiesPropertySource(url);
    }
}
