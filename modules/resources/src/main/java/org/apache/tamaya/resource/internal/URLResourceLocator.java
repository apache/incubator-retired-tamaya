package org.apache.tamaya.resource.internal;

import org.apache.tamaya.resource.ResourceLocator;

import javax.annotation.Priority;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * {@link ResourceLocator} for locating local files.
 */
@Priority(80)
public class URLResourceLocator implements ResourceLocator{
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(URLResourceLocator.class.getName());

    /**
     * Tries to evaluate the location passed by loading from the classloader.
     * @param classLoader the class loader to use
     * @param expression the path expression
     * @return the resources found.
     */
    @Override
    public Collection<URL> lookup(ClassLoader classLoader, String expression) {
        List<URL> resources = new ArrayList<>();
        try {
            File file = new File(expression);
            if (file.exists()) {
                resources.add(file.toURI().toURL());
            }
            return resources;
        } catch (IOException | RuntimeException e) {
            LOG.finest("Failed to load resource from file: " + expression);
            return Collections.emptySet();
        }
    }

}
