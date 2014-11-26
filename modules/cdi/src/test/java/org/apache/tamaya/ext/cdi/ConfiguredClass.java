package org.apache.tamaya.ext.cdi;

import org.apache.tamaya.annot.ConfigChanged;
import org.apache.tamaya.annot.ConfiguredProperty;
import org.apache.tamaya.annot.DefaultValue;
import org.apache.tamaya.annot.WithConfig;

import javax.inject.Singleton;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;

/**
 * Created by Anatole on 08.09.2014.
 */
@Singleton
public class ConfiguredClass{

    @WithConfig("test")
    @ConfiguredProperty
    private String testProperty;

    @ConfiguredProperty("a.b.c.key1")
    @ConfiguredProperty("a.b.c.key2")
    @ConfiguredProperty("a.b.c.key3")
    @DefaultValue("The current \\${JAVA_HOME} env property is ${env:JAVA_HOME}.")
    String value1;

    @WithConfig("test")
    @ConfiguredProperty("foo")
    @ConfiguredProperty("a.b.c.key2")
    private String value2;

    @ConfiguredProperty
    @DefaultValue("N/A")
    private String runtimeVersion;

    @ConfiguredProperty
    @DefaultValue("${sys:java.version}")
    private String javaVersion2;

    @ConfiguredProperty
    @DefaultValue("5")
    private Integer int1;

    @WithConfig("test")
    @ConfiguredProperty
    private int int2;

    @WithConfig("test")
    @ConfiguredProperty
    private boolean booleanT;

    @WithConfig("test")
    @ConfiguredProperty("BD")
    private BigDecimal bigNumber;

    @ConfigChanged
    public void changeListener1(PropertyChangeEvent configChange){
        // will be called
    }

    public String getTestProperty() {
        return testProperty;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public String getJavaVersion2() {
        return javaVersion2;
    }

    public Integer getInt1() {
        return int1;
    }

    public int getInt2() {
        return int2;
    }

    public boolean isBooleanT() {
        return booleanT;
    }

    public BigDecimal getBigNumber() {
        return bigNumber;
    }

    public String toString(){
        return super.toString() + ": testProperty="+testProperty+", value1="+value1+", value2="+value2
                +", int1="+int1+", int2="+int2+", booleanT="+booleanT+", bigNumber="+bigNumber
                +", runtimeVersion="+runtimeVersion+", javaVersion2="+javaVersion2;
    }

}
