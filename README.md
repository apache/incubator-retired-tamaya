# Apache Tamaya (incubating)

Tamaya is a very powerful yet flexible configuration solution. It's core is built based on a few simple concepts.
There are at least two main usage scenarios for Tamaya, which are synergetic:

- In an enterprise context you can easily implement a configuration architecture for your whole company and deploy the
  logic as an extension module. All application development teams in your company can then depend on this module (and the
  basic Tamaya core implementation). As a result all applications/modules in your company follow the same configuration
  policy, which makes it much more simpler to move people between your teams. Similarly additional tooling functionality
  can help you to manage configuration on application as well as on enterprise level, e.g. providing command line or
  REST support to access the supported configuration entries, types and values, configuration validation and more.
- If you are writing an application, application component or library you can support configuration using Tamaya by
  adding it as an optional dependency. If done so your users/customers can use Tamaya to connect their current enterprise
  configuration infrastructure transparently to your code. As an example you can use Tamaya to read your default
  configuration files, but since Tamaya is so easily extendable, customers can deploy an additional jar, which then
  allows them to add their own configuration mechanisms such as databases, datagrids or REST services.

More information on Tamaya can be found on the [homepage of Apache Tamaya](http://tamaya.incubator.apache.org/).

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



