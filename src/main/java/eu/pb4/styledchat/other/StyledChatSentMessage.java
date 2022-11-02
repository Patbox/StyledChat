package eu.pb4.styledchat.other;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface StyledChatSentMessage extends SentMessage {
    Text override();

    SignedMessage message();

    StyledChatSentMessage reformat(MessageType.Parameters sent);

    record Chat(SignedMessage message, Text override, MessageType.Parameters parameters) implements StyledChatSentMessage {

        public Text getContent() {
            return message.unsignedContent();
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                sender.networkHandler.sendChatMessage(signedMessage, this.parameters);
            }
        }

        @Override
        public StyledChatSentMessage reformat(MessageType.Parameters pars) {
            return new StyledChatSentMessage.Chat(message, override, pars);
        }
    }

    record System(SignedMessage message, Text override, MessageType.Parameters parameters) implements StyledChatSentMessage {

        public Text getContent() {
            return this.message.unsignedContent();
        }

        @Override
        public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
            sender.networkHandler.sendProfilelessChatMessage(message.getContent(), this.parameters);
        }

        @Override
        public StyledChatSentMessage reformat(MessageType.Parameters pars) {
            return new StyledChatSentMessage.Chat(message, override, pars);
        }
    }
}
