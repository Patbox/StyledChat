package eu.pb4.styledchat;

import eu.pb4.placeholders.TextParser;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

public class StyledChatEvents {
    /**
     * Event ran for message content before they are formatted (Strings)
     */
    public static final Event<PreMessageEvent> PRE_MESSAGE_CONTENT_SEND = EventFactory.createArrayBacked(PreMessageEvent.class, callbacks -> (message, player, filtered) -> {
        for (var callback : callbacks) {
            message = callback.onPreMessage(message, player, filtered);
        }

        return message;
    });

    /**
     * Event ran for message content after it being formatted (Text)
     */
    public static final Event<MessageEvent> MESSAGE_CONTENT_SEND = EventFactory.createArrayBacked(MessageEvent.class, callbacks -> (message, player, filtered) -> {
        for (var callback : callbacks) {
            message = callback.onMessage(message, player, filtered);
        }

        return message;
    });

    /**
     * Event ran before message is send to someone, it is fully formatted (including template)
     */
    public static final Event<MessageToEvent> MESSAGE_TO_SEND = EventFactory.createArrayBacked(MessageToEvent.class, callbacks -> (message, sender, receiver, filtered) -> {
        for (var callback : callbacks) {
            message = callback.onMessageTo(message, sender, receiver, filtered);
        }

        return message;
    });

    /**
     * This event can be used to allow custom formatting
     */
    public static final Event<FormattingCreationEvent> FORMATTING_CREATION_EVENT = EventFactory.createArrayBacked(FormattingCreationEvent.class, callbacks -> (player, builder) -> {
        for (var callback : callbacks) {
            callback.onFormattingBuild(player, builder);
        }
    });

    public interface FormattingCreationEvent {
        void onFormattingBuild(ServerPlayerEntity player, BiConsumer<String, TextParser.TextFormatterHandler> builder);
    }

    public interface PreMessageEvent {
        String onPreMessage(String message, ServerPlayerEntity player, boolean filtered);
    }

    public interface MessageEvent {
        Text onMessage(Text message, ServerPlayerEntity player, boolean filtered);
    }

    public interface MessageToEvent {
        Text onMessageTo(Text message, ServerPlayerEntity sender, ServerPlayerEntity receiver, boolean filtered);
    }
}
