package eu.pb4.styledchat.other;

import net.minecraft.network.message.SignedMessage;

public interface ExtendedSentMessage {
    SignedMessage styledChat$message();
    default void styledChat$setMessage(SignedMessage value) {};
}
