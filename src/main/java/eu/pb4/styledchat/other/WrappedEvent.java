package eu.pb4.styledchat.other;

import net.fabricmc.fabric.api.event.Event;

import java.util.function.Function;

public final class WrappedEvent<T> extends Event<T> {
    public final Event<T> event;

    public WrappedEvent(Event<T> event, Function<Event<T>, T> argModifier) {
        this.event = event;
        this.invoker = argModifier.apply(event);
    }

    @Override
    public void register(T listener) {
        this.event.register(listener);
    }
}
