/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.tamaya.examples.distributed;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by atsticks on 13.11.16.
 */
public class DisplayContent {
    public String displayId;
    public String title = "UNKNOWN";
    public Map<String,String> content = new HashMap<>();
    public long timestamp = System.currentTimeMillis();
    public String displayName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayContent)) return false;
        DisplayContent that = (DisplayContent) o;
        return timestamp == that.timestamp &&
                Objects.equals(displayId, that.displayId) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayId, title, timestamp);
    }

    @Override
    public String toString() {
        return "DisplayContent{" +
                "displayId='" + displayId + '\'' +
                ", title='" + title + '\'' +
                ", content=" + content +
                '}';
    }
}
