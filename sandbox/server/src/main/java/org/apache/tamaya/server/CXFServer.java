package org.apache.tamaya.server;

/*
HelloWorldImpl implementor = new HelloWorldImpl();
JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
svrFactory.setServiceClass(HelloWorld.class);
svrFactory.setAddress("http://localhost:9000/helloWorld");
svrFactory.setServiceBean(implementor);
svrFactory.getInInterceptors().add(new LoggingInInterceptor());
svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
svrFactory.create();
 */

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

public class CXFServer implements ConfigServer{

    private Server cxfEndpoint;

    public void start(int port) {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        ConfigService confService = new ConfigService();
        sf.setServiceBeanObjects(confService);
        sf.setAddress("http://localhost:"+port+"/");
        cxfEndpoint = sf.create();
    }

    public boolean isStarted(){
        if(cxfEndpoint!=null){
            return cxfEndpoint.isStarted();
        }
        return false;
    }

    public void stop(){
        if(cxfEndpoint!=null){
            cxfEndpoint.stop();
        }
    }

    public void destroy(){
        if(cxfEndpoint!=null){
            cxfEndpoint.destroy();
            cxfEndpoint = null;
        }
    }

    public static void main(String... args){
        new CXFServer().start(8888);
    }

}