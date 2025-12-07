package eu.pb4.styledchat.other;

import net.minecraft.network.chat.PlayerChatMessage;

public interface ExtendedSentMessage {
    PlayerChatMessage styledChat$message();
    default void styledChat$setMessage(PlayerChatMessage value) {};
}
