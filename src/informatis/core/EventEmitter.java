package informatis.core;

import arc.struct.*;
import informatis.SUtils;

public class EventEmitter<T> {
    final ObjectMap<T, ObjectSet<Runnable>> events = new ObjectMap<>();

    public Runnable subscribe(T event, Runnable listener) {
        ObjectSet<Runnable> listeners = events.get(event);
        if(listeners == null) events.put(event, ObjectSet.with(listener));
        else listeners.add(listener);
        return () -> unsubscribe(event, listener);
    }

    public void unsubscribe(T event, Runnable listener) {
        ObjectSet<Runnable> listeners = events.get(event);
        if(listeners == null) return;
        listeners.remove(listener);
    }
    

    public void fire(T event) {
        ObjectSet<Runnable> listeners = events.get(event);
        if(listeners == null) return;
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}