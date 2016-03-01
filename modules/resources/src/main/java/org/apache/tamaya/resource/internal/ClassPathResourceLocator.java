package org.apache.tamaya.resource.internal;

import org.apache.tamaya.resource.ResourceLocator;

import javax.annotation.Priority;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by atsticks on 01.03.16.
 */
@Priority(100)
public class ClassPathResourceLocator implements ResourceLocator{
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(ClassPathResourceLocator.class.getName());

    /**
     * Tries to evaluate the location passed by Ant path matching.
     * @param classLoader the class loader to use
     * @param expression the path expression
     * @return true, if the expression could be resolved.
     */
    @Override
    public Collection<URL> lookup(ClassLoader classLoader, String expression) {
        try {
            // 1: try file path
            Collection<URL> found = FileCollector.collectFiles(expression);
            if (found.isEmpty()) {
                found = new ClasspathCollector(classLoader).collectFiles(expression);
            }
            return found;
        } catch (RuntimeException e) {
            LOG.finest("Failed to load resource from CP: " + expression);
            return Collections.emptySet();
        }
    }

}
