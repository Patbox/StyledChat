package eu.pb4.styledchat.ducks;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface ExtSignedMessage {
    static Text getArg(SignedMessage message, String name) {
        return ((ExtSignedMessage) (Object) message).styledChat_getArg(name);
    }

    static void setArg(SignedMessage message, String name, Text value) {
        ((ExtSignedMessage) (Object) message).styledChat_setArg(name, value);
    }

    static ExtSignedMessage of(SignedMessage message) {
        return (ExtSignedMessage) (Object) message;
    }

    void styledChat_setArg(String name, Text arg);
    String styledChat_getOriginal();
    Text styledChat_getArg(String name);

    void styledChat_setType(RegistryKey<MessageType> type1);
    @Nullable
    RegistryKey<MessageType> styledChat_getType();

    void styledChat_setSource(ServerCommandSource source);
    @Nullable
    ServerCommandSource styledChat_getSource();
}
