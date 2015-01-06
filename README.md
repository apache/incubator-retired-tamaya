Building Apache Tamaya (incubating)



The Apache Tamaya project contains classes which are intended to be built with Java7 and others
which are for Java8. This means you need to have both JDK-1.7 and JDK-1.8 installed on your computer.

To tell maven which JDK it should use for each of the projects we do leverage the 
maven-toolchains-plugin and Mavens toolchains support.

See the following links for more information
http://maven.apache.org/ref/3.2.5/maven-core/toolchains.html
http://maven.apache.org/guides/mini/guide-using-toolchains.html

Please copy the provided toolchains.xml sample to ~.m2/toolchains.xml