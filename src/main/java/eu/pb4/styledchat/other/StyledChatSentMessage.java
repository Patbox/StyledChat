package eu.pb4.styledchat.other;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import eu.pb4.styledchat.ducks.ExtPlayerChatMessage;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Objects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

public interface StyledChatSentMessage extends OutgoingChatMessage, ExtendedSentMessage {
    Component override();

    PlayerChatMessage message();

    StyledChatSentMessage reformat(ChatType.Bound sent, ResourceKey<ChatType> sourceType);

    ResourceKey<ChatType> sourceType();

    @Override
    default PlayerChatMessage styledChat$message() {
        return this.message();
    }

    record Chat(PlayerChatMessage message, Component override, ChatType.Bound parameters, ResourceKey<ChatType> sourceType, MutableObject<ChatType.Bound> colorless) implements StyledChatSentMessage {
        public Component content() {
            return message.decoratedContent();
        }

        @Override
        public void sendToPlayer(ServerPlayer receiver, boolean filterMaskEnabled, ChatType.Bound params) {
            PlayerChatMessage signedMessage = this.message.filter(filterMaskEnabled);
            var color = ((ExtPlayNetworkHandler) receiver.connection).styledChat$chatColors();
            if (!color && colorless.getValue() == null) {
                colorless.setValue(StyledChatUtils.removeColor(parameters));
            }
            if (!signedMessage.isFullyFiltered()) {
                var id = receiver.registryAccess().lookupOrThrow(Registries.CHAT_TYPE).getKey(params.chatType().value());

                if (sourceType == null || Objects.equals(id, this.sourceType.identifier())) {
                    receiver.connection.sendPlayerChatMessage(signedMessage, color ? this.parameters : colorless.getValue());
                } else {
                    var baseInput = ExtPlayerChatMessage.getArg(signedMessage, "base_input");
                    var source = ExtPlayerChatMessage.of(signedMessage).styledChat_getSource();

                    var input = baseInput != StyledChatUtils.EMPTY_TEXT && baseInput.getContents() != PlainTextContents.EMPTY
                            ? baseInput
                            : signedMessage.decoratedContent();

                    var text = StyledChatStyles.getCustom(id, params.name(), input, params.targetName().orElse(null), source != null ? source : StyledChatMod.server.createCommandSourceStack());

                    if (!color) {
                        text = StyledChatUtils.removeColor(text);
                    }

                    receiver.connection.sendPlayerChatMessage(signedMessage, ChatType.bind(StyledChatMod.MESSAGE_TYPE_ID, receiver.registryAccess(), text));
                }
            }
        }

        @Override
        public StyledChatSentMessage reformat(ChatType.Bound pars, ResourceKey<ChatType> sourceType) {
            return new StyledChatSentMessage.Chat(message, override, pars, sourceType, new MutableObject<>());
        }
    }

    record System(PlayerChatMessage message, Component override, ChatType.Bound parameters, ResourceKey<ChatType> sourceType, MutableObject<ChatType.Bound> colorless) implements StyledChatSentMessage {
        public Component content() {
            return this.message.decoratedContent();
        }

        @Override
        public void sendToPlayer(ServerPlayer receiver, boolean filterMaskEnabled, ChatType.Bound params) {
            var id = receiver.registryAccess().lookupOrThrow(Registries.CHAT_TYPE).getKey(params.chatType().value());
            var color = ((ExtPlayNetworkHandler) receiver.connection).styledChat$chatColors();
            if (!color && colorless.getValue() == null) {
                colorless.setValue(StyledChatUtils.removeColor(parameters));
            }

            if (sourceType == null || Objects.equals(id, this.sourceType.identifier())) {
                receiver.connection.sendDisguisedChatMessage(message.decoratedContent(), color ? this.parameters : colorless.getValue());
            } else {
                var baseInput = ExtPlayerChatMessage.getArg(message, "base_input");
                var source = ExtPlayerChatMessage.of(message).styledChat_getSource();

                var input = baseInput != StyledChatUtils.EMPTY_TEXT && baseInput.getContents() != PlainTextContents.EMPTY
                        ? baseInput
                        : message.decoratedContent();

                var text = StyledChatStyles.getCustom(id, params.name(), input, params.targetName().orElse(null), source != null ? source : StyledChatMod.server.createCommandSourceStack());

                if (!color) {
                    text = StyledChatUtils.removeColor(text);
                }

                receiver.connection.sendDisguisedChatMessage(message.decoratedContent(), StyledChatUtils.createParameters(text));
            }
        }

        @Override
        public StyledChatSentMessage reformat(ChatType.Bound pars, ResourceKey<ChatType> sourceType) {
            return new StyledChatSentMessage.Chat(message, override, pars, sourceType, new MutableObject<>());
        }
    }


}
