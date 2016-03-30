package org.apache.tamaya.ui.event;


import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * Convenience class for accessing the _UI Scoped_ EventBus. If you are using something like the CDI event
 * bus, you don't need a class like this.
 */
public final class EventBus {

    private static final com.google.common.eventbus.EventBus EVENT_BUS =
            new com.google.common.eventbus.EventBus(new SubscriberExceptionHandler(){
                @Override
                public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
                    throwable.printStackTrace();
                }
            });

    private EventBus(){}

    public static void register(final Object listener) {
        EVENT_BUS.register(listener);
    }

    public static void unregister(final Object listener) {
        EVENT_BUS.unregister(listener);
    }

    public static void post(final Object event) {
        EVENT_BUS.post(event);
    }
}