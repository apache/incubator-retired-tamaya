package org.tamaya.examples.minimal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by Anatole on 20.03.2015.
 */
public class MinimalTests {

    private static Configuration config;

    @BeforeClass
    public static void before() throws InterruptedException {
        config = ConfigurationProvider.getConfiguration();
        Thread.sleep(100L);
    }

    @Test
    public void printMetaInfo() {
        System.out.println("****************************************************");
        System.out.println("Minimal Example");
        System.out.println("****************************************************");
        System.out.println();
        System.out.println("Example Metadata:");
        System.out.println("  Type        :  " + config.get("example.type"));
        System.out.println("  Name        :  " + config.get("example.name"));
        System.out.println("  Description :  " + config.get("example.description"));
        System.out.println("  Version     :  " + config.get("example.version"));
        System.out.println("  Author      :  " + config.get("example.author"));
        System.out.println();
    }

    @Test(expected = ConfigException.class)
    public void getNumberValueTooLong() {
        String value = config.get("example.number");
        System.err.println("**** example.number(String)=" + value);
        int number = config.getInteger("example.number").getAsInt();
        System.out.println("----\n   example.number(int)=" + number);
    }

    @Test
    public void getNumberValueAsInt_BadCase() {
        String value = config.get("example.numberAsHex");
        int number = config.getInteger("example.numberAsHex").getAsInt();
        print("example.numberAsHex", number);
    }

    @Test
    public void getNumberValueAsBigInteger() {
        String value = config.get("example.number");
        BigInteger number = config.get("example.number", BigInteger.class);
        print("example.number", number);
    }

    @Test(expected = ConfigException.class)
    public void getNumberValueAsLongHex() {
        String value = config.get("example.numberAsLongHex");
        long number = config.getInteger("example.numberAsLongHex").getAsInt();
        print("example.numberAsLongHex", number);
    }

    @Test
    public void getEnum() {
        String value = config.get("example.testEnum");
        TestEnum en = config.get("example.testEnum", TestEnum.class);
        print("example.testEnum", en);
    }

    protected void print(String key, Object value) {
        System.out.println("----\n" +
                "  " + key + "(String)=" + config.get(key)
                + "\n  " + key + "(" + value.getClass().getSimpleName() + ")=" + value);
    }
}
