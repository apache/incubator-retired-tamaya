package org.apache.tamaya.integration.cdi.internal;


import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * Tamaya main integreation with CDI, especially storing the BeanManager reference for implementation, where no
 * JNDI is available or {@code java:comp/env/BeanManager} is not set correctly.
 */
public class TamayaCDIIntegrationExtension implements Extension{
    /** The BeanManager references stored. */
    private static BeanManager beanManager;

    /**
     * Initializes the current BeanMaanager with the instance passed.
     * @param validation the event
     * @param beanManager the BeanManager instance
     */
    public void initBeanManager(@Observes AfterDeploymentValidation validation, BeanManager beanManager){
        TamayaCDIIntegrationExtension.beanManager = beanManager;
    }

    /**
     * Get the current {@link  BeanManager} instance.
     * @return
     */
    public static BeanManager getBeanManager(){
        return beanManager;
    }

}
