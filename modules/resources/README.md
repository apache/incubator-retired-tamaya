Apache Tamaya Resources Module
------------------------------

The Apache Tamaya resources module provides an additional service called 'ResourceLoader', which is accessible
from the  ServiceContext. The new service allows resolution of resources (modelled as URL) using Ant  styled
patterns:

* ? may represent any character (but there must be one)
* * may represent any character in the path (can be none or multiple)
* ** may be used to let the pattern matcher go down the hierarchy of files od resources in the current locations.

The resolver supports by default resolving paths in the file system and within the classpath, e.g.

  resources_testRoot/**/*.file
  c:\temp\**\*

In case of a conflict the resolver mechanism can also be explicitly addressed by adding the regarding prefix, so
the above expressions above are equivalent to

  classpath:resources_testRoot/**/*.file
  file:c:\temp\**\*

Most benefits are created, when also using the formats module, which provides an implementation of a 
PropertySourceProvider taking a set of paths to be resolved and a number of supported formats.


