Building Apache Tamaya (incubating)


The Apache Tamaya project contains modules which are intended to be used with Java8 and others
which are for Java7. 

This means if we like to release then we need to have both JDK-1.7 and JDK-1.8 
installed on your computer.

To tell Maven which JDK it should use for each of the projects we do leverage the 
maven-toolchains-plugin and Mavens toolchains support.

See the following links for more information
http://maven.apache.org/ref/3.2.5/maven-core/toolchains.html
http://maven.apache.org/guides/mini/guide-using-toolchains.html

The easiest way to setup your computer for being able to use toolchains is to 
simply copy the provided ./toolchains.xml sample to ~.m2/toolchains.xml and 
edit the paths to your own JDK installations.
We activate the maven-toolchains-plugin with a 'java7' profile in all 
the modules we need it.

For building the java7 modules with JDK-1.7 you simply need to build Tamaya with

$> mvn clean install -Pjava7

