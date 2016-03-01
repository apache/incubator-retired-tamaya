package org.apache.tamaya.resource.internal;

import org.apache.tamaya.resource.ResourceLocator;

import javax.annotation.Priority;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by atsticks on 01.03.16.
 */
@Priority(90)
public class FileResourceLocator implements ResourceLocator{
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(FileResourceLocator.class.getName());

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
            Enumeration<URL> urls = classLoader.getResources(expression);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                resources.add(url);
            }
            return resources;
        } catch (IOException | RuntimeException e) {
            LOG.finest("Failed to load resource from CP: " + expression);
            return Collections.emptySet();
        }
    }

}
