package org.apache.tamaya.integration.osgi;

import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.inject.api.ConfigDefault;

import javax.enterprise.inject.Default;

/**
 * Simple service to test injection in OSGI.
 */
public class HelloServiceImpl implements HelloService{

    @Config("example.message")
    @ConfigDefault("A Tamaya default.")
    private String message;

    @Override
    public String sayHello() {
        System.err.println("HELLO: " + message);
        return message;
    }
}
