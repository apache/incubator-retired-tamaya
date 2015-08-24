package org.apache.tamaya.server;

/**
 * Created by Anatole on 23.08.2015.
 */
public interface ConfigServer {

    void start(int port);
    boolean isStarted();
    void stop();
    void destroy();
}
