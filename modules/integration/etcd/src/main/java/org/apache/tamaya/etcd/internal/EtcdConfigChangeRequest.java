package org.apache.tamaya.etcd.internal;

import org.apache.tamaya.etcd.EtcdAccessor;
import org.apache.tamaya.etcd.EtcdBackends;
import org.apache.tamaya.mutableconfig.spi.AbstractConfigChangeRequest;

import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Change Request implementation based on etcd services.
 */
class EtcdConfigChangeRequest extends AbstractConfigChangeRequest{

    private static final Logger LOG = Logger.getLogger(EtcdConfigChangeRequest.class.getName());

    EtcdConfigChangeRequest(URI uri){
        super(uri);
    }

    @Override
    public boolean exists(String keyExpression) {
        for(EtcdAccessor accessor: EtcdBackends.getEtcdBackends()){
            try{
                Map<String,String> props = accessor.get(keyExpression);
                if(!props.containsKey("_ERROR")) {
                    // No repfix mapping necessary here, since we only access/return the value...
                    return props.get(keyExpression)!=null;
                }
            } catch(Exception e){
                LOG.log(Level.FINE, "etcd access failed on " + accessor.getUrl() + ", trying next...", e);
            }
        }
        return false;
    }


    @Override
    protected void commitInternal() {
        checkClosed();
        for(EtcdAccessor accessor: EtcdBackends.getEtcdBackends()){
            try{
                for(String k:getRemoved()){
                    Map<String,String> res = accessor.delete(k);
                    if(res.get("_ERROR")!=null){
                        LOG.info("Failed to remove key from etcd: " + k);
                    }
                }
                for(Map.Entry<String,String> en:getProperties().entrySet()){
                    Map<String,String> res = accessor.set(en.getKey(), en.getValue());
                    if(res.get("_ERROR")!=null){
                        LOG.info("Failed key from etcd: " + en.getKey()  + "=" + en.getValue());
                    }
                }
            } catch(Exception e){
                LOG.log(Level.FINE, "etcd access failed on " + accessor.getUrl() + ", trying next...", e);
            }
        }
    }

}
