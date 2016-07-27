#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
echo "Will replace http with https in the generated maven site ..."
if [ -z $1 ];
	then
	read -p "Please abort in case you haven't generated the site or press RETURN to continue.";
else
        echo "Assuming script is run on CI - starting to replace ..."
fi
echo "Where am I?"
pwd
cd target/site
echo "Changed to site directory ..."
sed -i 's/"http:/"https:/g' *.html
echo "DONE - please verify before pushing."
