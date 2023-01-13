package eu.pb4.styledchat.other;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.Objects;

public interface StyledChatSentMessage extends SentMessage {
    Text override();

    SignedMessage message();

    StyledChatSentMessage reformat(MessageType.Parameters sent, RegistryKey<MessageType> sourceType);

    RegistryKey<MessageType> sourceType();

    record Chat(SignedMessage message, Text override, MessageType.Parameters parameters, RegistryKey<MessageType> sourceType) implements StyledChatSentMessage {

        public Text getContent() {
            return message.unsignedContent();
        }

        @Override
        public void send(ServerPlayerEntity receiver, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            if (!signedMessage.isFullyFiltered()) {
                var id = receiver.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getId(params.type());

                if (sourceType == null || Objects.equals(id, this.sourceType.getValue())) {
                    receiver.networkHandler.sendChatMessage(signedMessage, this.parameters);
                } else {
                    var baseInput = ExtSignedMessage.getArg(signedMessage, "base_input");
                    var source = ExtSignedMessage.of(signedMessage).styledChat_getSource();

                    var input = baseInput != null && baseInput.getContent() != TextContent.EMPTY
                            ? baseInput
                            : signedMessage.getContent();


                    receiver.networkHandler.sendChatMessage(signedMessage, StyledChatUtils.createParameters(StyledChatStyles.getCustom(id, params.name(), input, params.targetName(), source != null ? source : StyledChatMod.server.getCommandSource())));
                }
            }
        }

        @Override
        public StyledChatSentMessage reformat(MessageType.Parameters pars, RegistryKey<MessageType> sourceType) {
            return new StyledChatSentMessage.Chat(message, override, pars, sourceType);
        }
    }

    record System(SignedMessage message, Text override, MessageType.Parameters parameters, RegistryKey<MessageType> sourceType) implements StyledChatSentMessage {
        public Text getContent() {
            return this.message.unsignedContent();
        }

        @Override
        public void send(ServerPlayerEntity receiver, boolean filterMaskEnabled, MessageType.Parameters params) {
            var id = receiver.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getId(params.type());

            if (sourceType == null || Objects.equals(id, this.sourceType.getValue())) {
                receiver.networkHandler.sendProfilelessChatMessage(message.getContent(), this.parameters);
            } else {
                var baseInput = ExtSignedMessage.getArg(message, "base_input");
                var source = ExtSignedMessage.of(message).styledChat_getSource();

                var input = baseInput != null && baseInput.getContent() != TextContent.EMPTY
                        ? baseInput
                        : message.getContent();

                receiver.networkHandler.sendChatMessage(message, StyledChatUtils.createParameters(StyledChatStyles.getCustom(id, params.name(), input, params.targetName(), source != null ? source : StyledChatMod.server.getCommandSource())));
            }
        }

        @Override
        public StyledChatSentMessage reformat(MessageType.Parameters pars, RegistryKey<MessageType> sourceType) {
            return new StyledChatSentMessage.Chat(message, override, pars, sourceType);
        }
    }
}
