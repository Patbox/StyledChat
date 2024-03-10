package eu.pb4.styledchat.other;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Objects;

public interface StyledChatSentMessage extends SentMessage, ExtendedSentMessage {
    Text override();

    SignedMessage message();

    StyledChatSentMessage reformat(MessageType.Parameters sent, RegistryKey<MessageType> sourceType);

    RegistryKey<MessageType> sourceType();

    @Override
    default SignedMessage styledChat$message() {
        return this.message();
    }

    record Chat(SignedMessage message, Text override, MessageType.Parameters parameters, RegistryKey<MessageType> sourceType, MutableObject<MessageType.Parameters> colorless) implements StyledChatSentMessage {
        public Text content() {
            return message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity receiver, boolean filterMaskEnabled, MessageType.Parameters params) {
            SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
            var color = ((ExtPlayNetworkHandler) receiver.networkHandler).styledChat$chatColors();
            if (!color && colorless.getValue() == null) {
                colorless.setValue(StyledChatUtils.removeColor(parameters));
            }
            if (!signedMessage.isFullyFiltered()) {
                var id = receiver.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getId(params.type());

                if (sourceType == null || Objects.equals(id, this.sourceType.getValue())) {
                    receiver.networkHandler.sendChatMessage(signedMessage, color ? this.parameters : colorless.getValue());
                } else {
                    var baseInput = ExtSignedMessage.getArg(signedMessage, "base_input");
                    var source = ExtSignedMessage.of(signedMessage).styledChat_getSource();

                    var input = baseInput != StyledChatUtils.EMPTY_TEXT && baseInput.getContent() != PlainTextContent.EMPTY
                            ? baseInput
                            : signedMessage.getContent();

                    var text = StyledChatStyles.getCustom(id, params.name(), input, params.targetName(), source != null ? source : StyledChatMod.server.getCommandSource());

                    if (!color) {
                        text = StyledChatUtils.removeColor(text);
                    }

                    receiver.networkHandler.sendChatMessage(signedMessage, StyledChatUtils.createParameters(text));
                }
            }
        }

        @Override
        public StyledChatSentMessage reformat(MessageType.Parameters pars, RegistryKey<MessageType> sourceType) {
            return new StyledChatSentMessage.Chat(message, override, pars, sourceType, new MutableObject<>());
        }
    }

    record System(SignedMessage message, Text override, MessageType.Parameters parameters, RegistryKey<MessageType> sourceType, MutableObject<MessageType.Parameters> colorless) implements StyledChatSentMessage {
        public Text content() {
            return this.message.getContent();
        }

        @Override
        public void send(ServerPlayerEntity receiver, boolean filterMaskEnabled, MessageType.Parameters params) {
            var id = receiver.server.getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getId(params.type());
            var color = ((ExtPlayNetworkHandler) receiver.networkHandler).styledChat$chatColors();
            if (!color && colorless.getValue() == null) {
                colorless.setValue(StyledChatUtils.removeColor(parameters));
            }

            if (sourceType == null || Objects.equals(id, this.sourceType.getValue())) {
                receiver.networkHandler.sendProfilelessChatMessage(message.getContent(), color ? this.parameters : colorless.getValue());
            } else {
                var baseInput = ExtSignedMessage.getArg(message, "base_input");
                var source = ExtSignedMessage.of(message).styledChat_getSource();

                var input = baseInput != StyledChatUtils.EMPTY_TEXT && baseInput.getContent() != PlainTextContent.EMPTY
                        ? baseInput
                        : message.getContent();

                var text = StyledChatStyles.getCustom(id, params.name(), input, params.targetName(), source != null ? source : StyledChatMod.server.getCommandSource());

                if (!color) {
                    text = StyledChatUtils.removeColor(text);
                }

                receiver.networkHandler.sendProfilelessChatMessage(message.getContent(), StyledChatUtils.createParameters(text));
            }
        }

        @Override
        public StyledChatSentMessage reformat(MessageType.Parameters pars, RegistryKey<MessageType> sourceType) {
            return new StyledChatSentMessage.Chat(message, override, pars, sourceType, new MutableObject<>());
        }
    }


}
