/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.internal.format;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.core.config.ConfigurationFormat;

public class PropertiesFormat implements ConfigurationFormat{

    private final static Logger LOG = Logger.getLogger(PropertiesFormat.class.getName());

    @Override
    public String getFormatName(){
        return "properties";
    }

    @Override
	public boolean isAccepted(Resource resource) {
		String path = resource.getFilename();
		return path != null && path.endsWith(".properties");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,String> readConfiguration(Resource resource) {
		if (isAccepted(resource) && resource.exists()) {
			try (InputStream is = resource.getInputStream()) {
				Properties p = new Properties();
				p.load(is);
				return Map.class.cast(p);
			} catch (Exception e) {
                LOG.log(Level.FINEST, e, () -> "Failed to read config from resource: " + resource);
			}
		}
		return Collections.emptyMap();
	}

}
