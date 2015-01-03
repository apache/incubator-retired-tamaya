package org.apache.tamaya.core.internal.resource;

import org.apache.tamaya.core.resources.Resource;
import org.apache.tamaya.core.resources.ResourceLoader;

import javax.annotation.Priority;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Logger;

/**
 * Simple default implementation of the resource loader.
 */
@Priority(0)
public class DefaultResourceLoader implements ResourceLoader{

    private static final Logger LOG = Logger.getLogger(DefaultResourceLoader.class.getName());

    private WeakHashMap<ClassLoader, PathMatchingResourcePatternResolver> resourceLoaders = new WeakHashMap<>();

    @Override
    public List<Resource> getResources(ClassLoader classLoader, Collection<String> expressions) {
        List<Resource> resources = new ArrayList<>();
        for(String expression:expressions){
            if(tryClassPath(classLoader, expression, resources) || tryFile(expression, resources) || tryURL(expression, resources)
              || tryAntPath(classLoader, expression, resources)){
                continue;
            }
            LOG.warning("Failed to resolve resource: " + expression);
        }
        return resources;
    }

    private boolean tryClassPath(ClassLoader classLoader, String expression, List<Resource> resources) {
        try{
            Enumeration<URL> urls = classLoader.getResources(expression);
            while(urls.hasMoreElements()){
                URL url = urls.nextElement();
                resources.add(new UrlResource(url));
            }
            return !resources.isEmpty();
        }
        catch(Exception e){
            LOG.finest(() -> "Failed to load resource from CP: " + expression);
        }
        return false;
    }

    private boolean tryFile(String expression, List<Resource> resources) {
        try{
            File file = new File(expression);
            if(file.exists()) {
                resources.add(new FileSystemResource(file));
                return true;
            }
        }
        catch(Exception e){
            LOG.finest(() -> "Failed to load resource from file: " + expression);
        }
        return false;
    }

    private boolean tryURL(String expression, List<Resource> resources) {
        try{
            URL url = new URL(expression);
            resources.add(new UrlResource(url));
            return true;
        }
        catch(Exception e){
            LOG.finest(() -> "Failed to load resource from file: " + expression);
        }
        return false;

    }

    private boolean tryAntPath(ClassLoader classLoader, String expression, List<Resource> resources) {
        PathMatchingResourcePatternResolver loader = resourceLoaders.computeIfAbsent(classLoader, cl -> new PathMatchingResourcePatternResolver(cl));
        try{
            resources.addAll(Arrays.asList(loader.getResources(expression)));
            return !resources.isEmpty();
        }
        catch(Exception e){
            LOG.finest(() -> "Failed to load resources from pattern: " + expression);
        }
        return false;
    }

}
