package eu.pb4.styledchat;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.commands.CommandSourceStack;

public class StyledChatEvents {
    /**
     * Event ran for message content before they are formatted (Strings)
     */
    public static final Event<PreMessageEvent> PRE_MESSAGE_CONTENT = EventFactory.createArrayBacked(PreMessageEvent.class, callbacks -> (message, player) -> {
        for (var callback : callbacks) {
            message = callback.onPreMessage(message, player);
        }

        return message;
    });

    /**
     * Event ran for message content after it being formatted (Text)
     */
    public static final Event<MessageEvent> MESSAGE_CONTENT = EventFactory.createArrayBacked(MessageEvent.class, callbacks -> (message, player) -> {
        for (var callback : callbacks) {
            message = callback.onMessage(message, player);
        }

        return message;
    });

    public interface PreMessageEvent {
        String onPreMessage(String message, PlaceholderContext context);
    }

    public interface MessageEvent {
        TextNode onMessage(TextNode message, PlaceholderContext context);
    }
}
