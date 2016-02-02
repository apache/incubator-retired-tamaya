/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.server;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Spring boot based configuration service that behavious compatible with etcd REST API (excluded the blocking API
 * calls).
 */
@Path("/")
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class ConfigurationResource {
    private final String scope;
    private final AtomicLong readCounter  = new AtomicLong();
    private final AtomicLong writeCounter  = new AtomicLong();
    private final AtomicLong deleteCounter  = new AtomicLong();

    public ConfigurationResource(String scope) {
        this.scope = scope;
    }

    @GET
    @Path("/version")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public String version() {
        return "{ \"version\" : \"Apache Tamaya: 0.2-incubating\" }";
    }

    @GET
    @Path("/v2/keys")
    public String readEtcdConfig(@QueryParam("recursive") Boolean recursive) {
        return readConfig(recursive);
    }

    /**
     *
     * This models a etcd2 compliant access point for getting a property value.
     * @return
     */
    @GET
    @Path("/keys")
    public String readConfig(@QueryParam("recursive") Boolean recursive) {
        readCounter.incrementAndGet();
        Configuration config = ConfigurationProvider.getConfiguration();
        Map<String,String> children = config.getProperties();
        JsonArrayBuilder ab = Json.createArrayBuilder();
        for(Map.Entry<String,String> en: children.entrySet()){
            Node node = new Node(config, en.getKey(), "node");
            ab.add(node.createJsonObject());
        }
        Node node = new Node(config, null, "node", ab.build());
        JsonObjectBuilder root = Json.createObjectBuilder().add("action", "get")
                .add("node", node.createJsonObject());
        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.writeObject(root.build());
        return writer.toString();
    }

    /**
     * This models a etcd2 compliant access point for getting a property value.
     * @param key
     * @return
     */
    @GET
    @Path("/v2/keys/{key}")
    public String readEtcdConfig(@PathParam("key") String key, @QueryParam("recursive") Boolean recursive) {
        return readConfig(key, recursive);
    }

    /**
     * This models a etcd2 compliant access point for getting a property value.
     * @param key
     * @return
     */
    @GET
    @Path("/keys/{key}")
    public String readConfig(@PathParam("key") String key, @QueryParam("recursive") Boolean recursive) {
        readCounter.incrementAndGet();
        Configuration config = ConfigurationProvider.getConfiguration();
        if(key!=null) {
            if (key.startsWith("/")) {
                key = key.substring(1);
            }
            if (config.get(key) != null && !recursive) {
                Node node = new Node(config, key, "node");
                JsonObjectBuilder root = Json.createObjectBuilder().add("action", "get")
                        .add("node", node.createJsonObject());
                StringWriter writer = new StringWriter();
                JsonGenerator gen = Json.createGenerator(writer);
                gen.write(root.build());
                return writer.toString();
            }
        }
        Map<String,String> children = null;
        if(key==null){
            children = config.getProperties();
        } else{
            children = config.with(ConfigurationFunctions.section(key)).getProperties();
        }
        JsonArrayBuilder ab = Json.createArrayBuilder();
        for(Map.Entry<String,String> en: children.entrySet()){
            Node node = new Node(config, en.getKey(), "node");
            ab.add(node.createJsonObject());
        }
        Node node = new Node(config, key, "node", ab.build());
        JsonObjectBuilder root = Json.createObjectBuilder().add("action", "get")
                .add("node", node.createJsonObject());
        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.writeObject(root.build());
        return writer.toString();
    }

    @PUT
    @Path("/v2/keys/{key}")
    public String writeEtcdConfig(@PathParam("key") String key, @javax.ws.rs.FormParam("value") String value,
                              @FormParam("ttl") Integer ttl) {
        return writeConfig(key, value, ttl);
    }
    /**
     * This models a etcd2 compliant access point for getting a property value:
     * <pre>
     *     {
     "action": "set",
     "node": {
     "createdIndex": 3,
     "key": "/message",
     "modifiedIndex": 3,
     "value": "Hello etcd"
     },
     "prevNode": {
     "createdIndex": 2,
     "key": "/message",
     "value": "Hello world",
     "modifiedIndex": 2
     }
     }
     * </pre>
     * @param key
     * @return
     */
    @PUT
    @Path("/keys/{key}")
    public String writeConfig(@PathParam("key") String key, @javax.ws.rs.FormParam("value") String value,
                              @FormParam("ttl") Integer ttl) {
        writeCounter.incrementAndGet();
        Configuration config = ConfigurationProvider.getConfiguration();
        if(key.startsWith("/")){
            key = key.substring(1);
        }
        Node prevNode = new Node(config, key, "prevNode");
        // TODO implement write! value and ttl as input
        Node node = new Node(config, key, "node");
        JsonObjectBuilder root = Json.createObjectBuilder().add("action", "set")
                .add("node", node.createJsonObject())
                .add("prevNode", prevNode.createJsonObject());
        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.writeObject(root.build());
        return writer.toString();
    }

    @DELETE
    @Path("/v2/keys/{key}")
    public String deleteEtcdConfig(@PathParam("key") String key) {
        return deleteConfig(key);
    }
    /**
     * This models a etcd2 compliant access point for getting a property value:
     * <pre>
     *     {
     "action": "set",
     "node": {
     "createdIndex": 3,
     "key": "/message",
     "modifiedIndex": 3,
     "value": "Hello etcd"
     },
     "prevNode": {
     "createdIndex": 2,
     "key": "/message",
     "value": "Hello world",
     "modifiedIndex": 2
     }
     }
     * </pre>
     * @param key
     * @return
     */
    @DELETE
    @Path("/keys/{key}")
    public String deleteConfig(@PathParam("key") String key) {
        deleteCounter.incrementAndGet();
        Configuration config = ConfigurationProvider.getConfiguration();
        if(key.startsWith("/")){
            key = key.substring(1);
        }
        Node prevNode = new Node(config, key, "prevNode");
        // TODO implement write! value and ttl as input
        Node node = new Node(config, key, "node");
        JsonObjectBuilder root = Json.createObjectBuilder().add("action", "delete")
                .add("node", node.createJsonObject())
                .add("prevNode", prevNode.createJsonObject());
        StringWriter writer = new StringWriter();
        JsonWriter jwriter = Json.createWriter(writer);
        jwriter.writeObject(root.build());
        return writer.toString();
    }

    public long getDeleteCounter(){
        return deleteCounter.get();
    }

    public long getReadCounter(){
        return readCounter.get();
    }

    public long getWriteCounter(){
        return writeCounter.get();
    }

    /**
     * Internal representation of a configuration node as modelled by etc.
     */
    private static final class Node{
        private Integer createdIndex;
        private Integer modifiedIndex;
        private String key;
        private String value;
        private String nodeId;
        private Integer ttl;
        private String expiration;
        private JsonArray nodes;

        Node(Configuration config, String key, String nodeId){
            this(config, key, nodeId, null);
        }
        Node(Configuration config, String key, String nodeId, JsonArray nodes){
            this.key = key;
            this.nodeId = Objects.requireNonNull(nodeId);
            if(key!=null) {
                value = config.get(key);
                createdIndex = config.getOrDefault("_" + key + ".createdIndex", Integer.class, null);
                modifiedIndex = config.getOrDefault("_" + key + ".modifiedIndex", Integer.class, null);
                ttl = config.getOrDefault("_" + key + ".ttl", Integer.class, null);
                expiration = config.getOrDefault("_" + key + ".expiration", null);
            }
            this.nodes = nodes;
        }

        JsonObject createJsonObject(){
            JsonObjectBuilder nodeBuilder = Json.createObjectBuilder();
            if(key!=null) {
                nodeBuilder.add("key", '/' + key);
            }else{
                nodeBuilder.add("dir", true);
            }
            if(value!=null){
                nodeBuilder.add("value", value);
            }
            if(createdIndex!=null){
                nodeBuilder.add("createdIndex", createdIndex.intValue());
            }
            if(modifiedIndex!=null){
                nodeBuilder.add("modifiedIndex", modifiedIndex.intValue());
            }
            if(ttl!=null){
                nodeBuilder.add("ttl", ttl.intValue());
            }
            if(expiration!=null){
                nodeBuilder.add("expiration", value);
            }
            if(nodes!=null){
                nodeBuilder.add("nodes", nodes);
            }
            return nodeBuilder.build();
        }
    }

}