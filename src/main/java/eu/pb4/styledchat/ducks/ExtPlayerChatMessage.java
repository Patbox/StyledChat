package eu.pb4.styledchat.ducks;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface ExtPlayerChatMessage {
    static Component getArg(PlayerChatMessage message, String name) {
        return ((ExtPlayerChatMessage) (Object) message).styledChat_getArg(name);
    }

    static void setArg(PlayerChatMessage message, String name, Component value) {
        ((ExtPlayerChatMessage) (Object) message).styledChat_setArg(name, value);
    }

    static ExtPlayerChatMessage of(PlayerChatMessage message) {
        return (ExtPlayerChatMessage) (Object) message;
    }

    void styledChat_setArg(String name, Component arg);
    String styledChat_getOriginal();
    Component styledChat_getArg(String name);

    void styledChat_setType(ResourceKey<ChatType> type1);
    @Nullable
    ResourceKey<ChatType> styledChat_getType();

    void styledChat_setSource(CommandSourceStack source);
    @Nullable
    CommandSourceStack styledChat_getSource();
}
