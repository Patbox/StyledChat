package eu.pb4.styledchat.ducks;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

public interface ExtSignedMessage {
    static Text getArg(SignedMessage message, String name) {
        return ((ExtSignedMessage) (Object) message).styledChat_getArg(name);
    }

    static void setArg(SignedMessage message, String name, Text value) {
        ((ExtSignedMessage) (Object) message).styledChat_setArg(name, value);
    }

    //void styledChat_setOriginal(String message);
    void styledChat_setArg(String name, Text arg);
    String styledChat_getOriginal();
    Text styledChat_getArg(String name);
}
