# Apache Tamaya (incubating)

Tamaya is a very powerful yet flexible configuration solution. It's core is built based on a few simple concepts. More information on Tamaya can be found
on the [homepage of Apache Tamaya](http://tamaya.incubator.apache.org/).

## Building Apache Tamaya

The Apache Tamaya project contains modules which are intended to be used with Java 8 and others
which are for Java 7.

If you would like to build Tamaya you need to have both JDK 1.7 and JDK-1.8 
installed on your computer.

To tell Maven which JDK it should use for each of the projects we do leverage the 
[Maven Toolchain Plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/) and Mavens toolchains support.

See the following links for more information

- [http://maven.apache.org/ref/3.2.5/maven-core/toolchains.html]()
- [http://maven.apache.org/guides/mini/guide-using-toolchains.html]()

The easiest way to setup your computer for being able to use toolchains is to 
simply copy the provided `./toolchains.xml` sample to `~.m2/toolchains.xml` and 
edit the paths to your own JDK installations.

If your Maven toolchain is installed correctly you can build Tamaya by the following command:

    $> maven clean install



