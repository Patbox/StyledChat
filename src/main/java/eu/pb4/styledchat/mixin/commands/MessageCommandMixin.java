package eu.pb4.styledchat.mixin.commands;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.other.StyledChatSentMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(method = "execute", at = @At("HEAD"))
    private static void styledChat_formatOgText(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, SignedMessage signedMessage, CallbackInfo ci) {
        var input = StyledChatUtils.maybeFormatFor(serverCommandSource, signedMessage.getSignedContent(), signedMessage.getContent());
        ExtSignedMessage.setArg(signedMessage, "base_input", input);
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_noopFeedback(ServerCommandSource instance, SentMessage message, boolean bl, MessageType.Parameters parameters) {
        // noop
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_formatText(ServerPlayerEntity instance, SentMessage message, boolean bl, MessageType.Parameters parameters, ServerCommandSource source) {
        if (message instanceof StyledChatSentMessage styledChatSentMessage) {
            try {
                var sent = StyledChatMod.getMessageType().params(StyledChatStyles.getPrivateMessageSent(
                        source.getDisplayName(),
                        instance.getDisplayName(),
                        ExtSignedMessage.getArg(styledChatSentMessage.message(), "base_input"), instance.getCommandSource()
                ));


                source.sendChatMessage(styledChatSentMessage.reformat(sent), bl, sent);

                var rex = StyledChatMod.getMessageType().params(StyledChatStyles.getPrivateMessageReceived(
                        source.getDisplayName(),
                        instance.getDisplayName(),
                        ExtSignedMessage.getArg(styledChatSentMessage.message(), "base_input"), source
                ));

                instance.sendChatMessage(styledChatSentMessage.reformat(rex), bl, rex);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        instance.sendChatMessage(message, bl, parameters);
    }
}
