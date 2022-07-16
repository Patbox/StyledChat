package eu.pb4.styledchat.mixin;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.other.WrappedEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(ServerMessageEvents.class)
public class ServerMessageEventsMixin {
    @Mutable
    @Shadow @Final public static Event<ServerMessageEvents.AllowChatMessage> ALLOW_CHAT_MESSAGE;

    @Mutable
    @Shadow @Final public static Event<ServerMessageEvents.AllowCommandMessage> ALLOW_COMMAND_MESSAGE;

    @Mutable
    @Shadow @Final public static Event<ServerMessageEvents.ChatMessage> CHAT_MESSAGE;

    @Mutable
    @Shadow @Final public static Event<ServerMessageEvents.CommandMessage> COMMAND_MESSAGE;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void styledChat_replaceEvents(CallbackInfo ci) {
        ALLOW_CHAT_MESSAGE = new WrappedEvent<>(ALLOW_CHAT_MESSAGE,
                e -> (message, sender, typeKey) -> e.invoker().allowChatMessage(StyledChatUtils.toEventMessage(message, PlaceholderContext.of(sender)), sender, typeKey));

        ALLOW_COMMAND_MESSAGE = new WrappedEvent<>(ALLOW_COMMAND_MESSAGE,
                e -> (message, sender, typeKey) -> e.invoker().allowCommandMessage(StyledChatUtils.toEventMessage(message, PlaceholderContext.of(sender)), sender, typeKey));

        CHAT_MESSAGE = new WrappedEvent<>(CHAT_MESSAGE,
                e -> (message, sender, typeKey) -> e.invoker().onChatMessage(StyledChatUtils.toEventMessage(message, PlaceholderContext.of(sender)), sender, typeKey));

        COMMAND_MESSAGE = new WrappedEvent<>(COMMAND_MESSAGE,
                e -> (message, sender, typeKey) -> e.invoker().onCommandMessage(StyledChatUtils.toEventMessage(message, PlaceholderContext.of(sender)), sender, typeKey));

    }
}
