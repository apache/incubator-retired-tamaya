## About Apache Tamaya

Tamaya provides a flexible and powerful Configuration Solution 
for Java developers using Java SE as well as for more complex 
usage scenarios like Cloud or Java EE. It provides a modern 
type-safe property based Configuration API combined with a 
powerful environment model and a flexible SPI. 

## Features

* Unified Configuration API
* Pluggable Configuration Backends
* Enforceable Configuration Policies
* Configuration Validation and Documentation
* Seemless Enterprise Integration

## Documentation

* [Use Cases](userguide/usecases.html)
* [High Level Design](userguide/HighLevelDesign.html)
* [API](userguide/API.html)
* [Extensions](extensions/index.html)

---

## Quickstart

Using Apache Tamaya is simple:

1. Add `org.apache.tamaya:tamaya-core:${{project.version}}` to your dependencies.
2. Add your config to `META-INF/javaconfiguration.properties`
3. Access your configuration by `ConfigurationProvider.getConfiguration()` and use it.
4. Look at the [extension modules](extensions/index.html) to customize your setup!
5. Enjoy!


## Rationale

Configuration is one of the most prominent cross-cutting concerns similar to logging. Most of us already have been
writing similar code again and again in each of our projects. Sometimes in a similar way but mostly always slightly
different, but certainly with high coupling to your configuration backends. Given your code is reused or integrated
some how, or deployed by some customers, struggling starts: not supported backends, different policies, missing
combination and validation mechanisms and so on. Tamaya solves all this by defining a common API and backend SPI.
Your code is decoupled from the configuratoin backend. There is no difference if you are running on your dev box
or in a clustered Docker environment in production, your code stays the same! There is no difference if you are writing
some small component or a complete product solution, your API to your configuration data stays the same! Once you
use Tamaya you will ask yourself why this did not exist before...


