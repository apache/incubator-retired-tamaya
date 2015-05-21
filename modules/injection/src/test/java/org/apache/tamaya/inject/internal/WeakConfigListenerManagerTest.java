package org.apache.tamaya.inject.internal;

import org.apache.tamaya.core.propertysource.SystemPropertySource;
import org.apache.tamaya.event.PropertyChangeSet;
import org.apache.tamaya.event.PropertyChangeSetBuilder;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

public class WeakConfigListenerManagerTest {

    private Consumer<PropertyChangeSet> consumer = s -> loggedSet = s;
    private PropertyChangeSet loggedSet;

    @Test
    public void testOf() throws Exception {
        assertNotNull(WeakConfigListenerManager.of());
    }

    @Test
    public void testRegisterConsumer() throws Exception {
        SystemPropertySource sysSrc = new SystemPropertySource();
        WeakConfigListenerManager.of().registerConsumer(this,
                s -> loggedSet = s);
        PropertyChangeSet change = PropertyChangeSetBuilder.of(sysSrc).add("aaa", "aaaValue").build();
        WeakConfigListenerManager.of().registerConsumer(this, consumer);
        WeakConfigListenerManager.of().publishChangeEvent(change);
        assertNotNull(loggedSet);
        loggedSet = null;
        WeakConfigListenerManager.of().unregisterConsumer(consumer);
    }

    @Test
    public void testUnregisterConsumer() throws Exception {
        SystemPropertySource sysSrc = new SystemPropertySource();
        PropertyChangeSet change = PropertyChangeSetBuilder.of(sysSrc).add("aaa", "aaaValue").build();
        Consumer<PropertyChangeSet> tempConsumer = s -> loggedSet = s;
        WeakConfigListenerManager.of().registerConsumer(this, tempConsumer);
        WeakConfigListenerManager.of().publishChangeEvent(change);
        assertNotNull(loggedSet);
        loggedSet = null;
        WeakConfigListenerManager.of().unregisterConsumer(this);
        assertNull(loggedSet);
        WeakConfigListenerManager.of().publishChangeEvent(change);
        assertNull(loggedSet);
    }

    @Test
    public void testPublishChangeEvent() throws Exception {
        SystemPropertySource sysSrc = new SystemPropertySource();
        WeakConfigListenerManager.of().publishChangeEvent(
                PropertyChangeSetBuilder.of(sysSrc).add("aaa", "aaaValue").build());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(WeakConfigListenerManager.of());
    }
}