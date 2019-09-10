<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
# Apache Tamaya (incubating)

Tamaya is a very powerful yet flexible configuration solution. Its core is built based on a few simple concepts.
There are at least two main usage scenarios for Tamaya, which are synergetic:

- In an enterprise context you can easily implement a configuration architecture for your whole company and deploy the
  logic as an extension module. All application development teams in your company can then depend on this module (and the
  basic Tamaya core implementation). As a result all applications/modules in your company follow the same configuration
  policy, which makes it much more simpler to move people between your teams. Similarly additional tooling functionality
  can help you to manage configuration on application as well as on enterprise level, e.g. providing command line or
  REST support to access the supported configuration entries, types and values, configuration configModel and more.
- If you are writing an application, application component or library you can support configuration using Tamaya by
  adding it as an optional dependency. If done so your users/customers can use Tamaya to connect their current enterprise
  configuration infrastructure transparently to your code. As an example you can use Tamaya to read your default
  configuration files, but since Tamaya is so easily extendable, customers can deploy an additional jar, which then
  allows them to add their own configuration mechanisms such as databases, datagrids or REST services.

More information on Tamaya can be found on the [homepage of Apache Tamaya](https://tamaya.incubator.apache.org/).

## Building Apache Tamaya

The Apache Tamaya project is built with [Maven 3](https://maven.apache.org/) and [Java 8](https://java.sun.com/), so you need JDK >=1.8 and a reasonable version of maven
installed on your computer.

## Local builds

Then you can build Tamaya via:
```
$ export MAVEN_OPTS="-Xmx512m"
$ mvn
```

### Travis / CI / badges

Apart from integration into ASF CI there's a travis build:

[![Build Status](https://travis-ci.org/apache/incubator-tamaya.svg?branch=master)](https://travis-ci.org/apache/incubator-tamaya/branches)

Sonarcloud integration:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=apache_incubator-tamaya&metric=alert_status)](https://sonarcloud.io/dashboard?id=apache_incubator-tamaya)
