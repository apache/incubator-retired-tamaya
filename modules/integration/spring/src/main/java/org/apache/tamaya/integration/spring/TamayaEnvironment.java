package org.apache.tamaya.integration.spring;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by atsticks on 12.11.15.
 */
public class TamayaEnvironment extends StandardEnvironment{

    protected void customizePropertySources(MutablePropertySources propertySources) {
        super.customizePropertySources(propertySources);
        propertySources.addLast(new TamayaSpringPropertySource());
    }
}
