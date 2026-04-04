package com.jelly.mightyminerv2.event;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight Forge-style event bus for 1.21.10 Fabric port.
 * Usage: EventBus.INSTANCE.register(obj)  /  EventBus.INSTANCE.post(event)
 */
public class EventBus {
    public static final EventBus INSTANCE = new EventBus();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface SubscribeEvent {
        boolean receiveCanceled() default false;
    }

    private final Map<Class<?>, List<MethodEntry>> handlers = new ConcurrentHashMap<>();
    private final Set<Object> registered = Collections.newSetFromMap(new WeakHashMap<>());

    public void register(Object obj) {
        if (registered.contains(obj)) return;
        registered.add(obj);
        Class<?> cls = obj instanceof Class ? (Class<?>) obj : obj.getClass();
        for (Method m : cls.getDeclaredMethods()) {
            if (!m.isAnnotationPresent(SubscribeEvent.class)) continue;
            if (m.getParameterCount() != 1) continue;
            m.setAccessible(true);
            Class<?> evtType = m.getParameterTypes()[0];
            handlers.computeIfAbsent(evtType, k -> new ArrayList<>())
                    .add(new MethodEntry(obj instanceof Class ? null : obj, m,
                            m.getAnnotation(SubscribeEvent.class).receiveCanceled()));
        }
    }

    public <T extends MMEvent> T post(T event) {
        List<MethodEntry> list = handlers.get(event.getClass());
        if (list == null) return event;
        for (MethodEntry e : list) {
            if (event instanceof Cancellable && ((Cancellable) event).isCanceled() && !e.receiveCanceled) continue;
            try { e.method.invoke(e.instance, event); }
            catch (Exception ex) { ex.printStackTrace(); }
        }
        return event;
    }

    private record MethodEntry(Object instance, Method method, boolean receiveCanceled) {}

    public interface Cancellable {
        boolean isCanceled();
        void setCanceled(boolean val);
    }
}
