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
package org.apache.tamaya.core.config;

import java.io.Serializable;

/**
 * This class models a configuration location as an immutable value type. Hereby
 * the location defines
 * <ul>
 * <li>the <b>reader</b>, identified by the unique readerId, which implements
 * the mechanism, how configuration is read.
 * <li>the <b>location</b> configures the reader, what resources to read.
 * </ul>
 */
public final class ConfigLocation implements Serializable {

	private static final long serialVersionUID = 1L;
	private String readerId;
	private String locationId;

	public ConfigLocation(String readerId, String locationId) {
		if (readerId == null) {
			throw new NullPointerException("readerID null.");
		}
		if (locationId == null) {
			throw new NullPointerException("locationId null.");
		}
		this.readerId = readerId;
		this.locationId = locationId;
	}

	/**
	 * @return the readerId
	 */
	public final String getReaderId() {
		return readerId;
	}

	/**
	 * @return the locationId
	 */
	public final String getLocationId() {
		return locationId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result
				+ ((readerId == null) ? 0 : readerId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigLocation other = (ConfigLocation) obj;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (readerId == null) {
			if (other.readerId != null)
				return false;
		} else if (!readerId.equals(other.readerId))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfigLocation [readerId=" + readerId + ", locationId="
				+ locationId + "]";
	}
}