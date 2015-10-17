package org.apache.tamaya.inject.api;

/**
 * Policy to control how new values are applied to a {@link DynamicValue}.
 */
public enum UpdatePolicy {
    /** New values are applied immedately and registered listeners are informed about the change. */
    IMMEDEATE,
    /** New values or not applied, but stored in the newValue property. Explcit call to DynamicValue#commit
     of DynamicValue#commitAndGet are required to accept the change and inform the listeners about the change.
     * Registered listeners will be informed, when the commit was performed explicitly.
     */
    EXPLCIT,
    /**
     * New values are always immedately discarded, listeners are not triggered.
     */
    NEVER,
    /**
     * All listeners are informed about the change encountered, but the value will not be applied.
     */
    LOG_ONLY
}
