#!/bin/sh

mvn -P sandbox -Djqassistant.store.directory=jqassistant.datastore \
    com.buschmais.jqassistant.scm:jqassistant-maven-plugin:server
