package org.apache.tamaya.etcd;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by atsticks on 15.01.16.
 */
public final class EtcdBackends {

    private static final Logger LOG = Logger.getLogger(EtcdBackends.class.getName());
    private static List<EtcdAccessor> etcdBackends = new ArrayList<>();

    static{
        int timeout = 2;
        String val = System.getProperty("tamaya.etcd.timeout");
        if(val == null){
            val = System.getenv("tamaya.etcd.timeout");
        }
        if(val!=null){
            timeout = Integer.parseInt(val);
        }
        String serverURLs = System.getProperty("tamaya.etcd.server.urls");
        if(serverURLs==null){
            serverURLs = System.getenv("tamaya.etcd.server.urls");
        }
        if(serverURLs==null){
            serverURLs = "http://127.0.0.1:4001";
        }
        for(String url:serverURLs.split("\\,")) {
            try{
                etcdBackends.add(new EtcdAccessor(url.trim(), timeout));
                LOG.info("Using etcd endoint: " + url);
            } catch(Exception e){
                LOG.log(Level.SEVERE, "Error initializing etcd accessor for URL: " + url, e);
            }
        }
    }

    private EtcdBackends(){}

    public static List<EtcdAccessor> getEtcdBackends(){
        return etcdBackends;
    }
}
