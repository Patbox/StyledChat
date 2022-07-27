package eu.pb4.styledchat.ducks;

import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;

public interface ExtSentMessage {
    static SignedMessage getWrapped(SentMessage x) {
        return ((ExtSentMessage) x).styledChat_getMessage();
    }

    SignedMessage styledChat_getMessage();
}
