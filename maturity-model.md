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
# The Apache Project Maturity Model
Each item in the model has a unique ID to allow them to be easily referenced elsewhere.

For reference, see the [Apache Project Maturity Model](http://community.apache.org/apache-way/apache-project-maturity-model.html)

## Code
### CD10
The project produces Open Source software, for distribution to the public at no charge.
### CD20
The project's code is easily discoverable and publicly accessible.
### CD30
The code can be built in a reproducible way using widely available standard tools.
### CD40
The full history of the project's code is available via a source code control system, in a way that allows any released version to be recreated.
### CD50
The provenance of each line of code is established via the source code control system, in a reliable way based on strong authentication of the committer. When third-party contributions are committed, commit messages provide reliable information about the code provenance.

## Licenses and Copyright
### LC10
The code is released under the Apache License, version 2.0.
### LC20
Libraries that are mandatory dependencies of the project's code do not create more restrictions than the Apache License does.
### LC30
The libraries mentioned in LC20 are available as Open Source software.
### LC40
Committers are bound by an Individual Contributor Agreement (the "Apache iCLA") that defines which code they are allowed to commit and how they need to identify code that is not their own.
### LC50
The copyright ownership of everything that the project produces is clearly defined and documented.

## Releases
### RE10
Releases consist of source code, distributed using standard and open archive formats that are expected to stay readable in the long term.
### RE20
Releases are approved by the project's PMC (see CS10), in order to make them an act of the Foundation.
### RE30
Releases are signed and/or distributed along with digests that can be reliably used to validate the downloaded archives.
### RE40
Convenience binaries can be distributed alongside source code but they are not Apache Releases -- they are just a convenience provided with no guarantee.
### RE50
The release process is documented and repeatable to the extent that someone new to the project is able to independently generate the complete set of artifacts required for a release.

## Quality
### QU10
The project is open and honest about the quality of its code. Various levels of quality and maturity for various modules are natural and acceptable as long as they are clearly communicated.
### QU20
The project puts a very high priority on producing secure software.
### QU30
The project provides a well-documented channel to report security issues, along with a documented way of responding to them.
### QU40
The project puts a high priority on backwards compatibility and aims to document any incompatible changes and provide tools and documentation to help users transition to new features.
### QU50
The project strives to respond to documented bug reports in a timely manner.

## Community
### CO10
The project has a well-known homepage that points to all the information required to operate according to this maturity model.
### CO20
The community welcomes contributions from anyone who acts in good faith and in a respectful manner and adds value to the project.
### CO30
Contributions include not only source code, but also documentation, constructive bug reports, constructive discussions, marketing and generally anything that adds value to the project.
### CO40
The community is meritocratic and over time aims to give more rights and responsibilities to contributors who add value to the project.
### CO50
The way in which contributors can be granted more rights such as commit access or decision power is clearly documented and is the same for all contributors.
### CO60
The community operates based on consensus of its members (see CS10) who have decision power. Dictators, benevolent or not, are not welcome in Apache projects.
### CO70
The project strives to answer user questions in a timely manner.

## Consensus Building
### CS10
The project maintains a public list of its contributors who have decision power -- the project's PMC (Project Management Committee) consists of those contributors.
### CS20
Decisions are made by consensus among PMC members 9 and are documented on the project's main communications channel. Community opinions are taken into account but the PMC has the final word if needed.
### CS30
Documented voting rules are used to build consensus when discussion is not sufficient.
### CS40
In Apache projects, vetoes are only valid for code commits and are justified by a technical explanation, as per the Apache voting rules defined in CS30.
### CS50
All "important" discussions happen asynchronously in written form on the project's main communications channel. Offline, face-to-face or private discussions that affect the project are also documented on that channel.

## Independence
### IN10
The project is independent from any corporate or organizational influence.
### IN20
Contributors act as themselves as opposed to representatives of a corporation or organization.
