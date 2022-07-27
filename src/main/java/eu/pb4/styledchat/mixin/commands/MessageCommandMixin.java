package eu.pb4.styledchat.mixin.commands;

import eu.pb4.styledchat.ducks.ExtSentMessage;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
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
    @Inject(method = "method_44144", at = @At("HEAD"))
    private static void styledChat_formatOgText(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, MessageType.Parameters parameters, SignedMessage signedMessage, CallbackInfo ci) {
        var input = StyledChatUtils.maybeFormatFor(serverCommandSource, ((ExtSignedMessage) (Object) signedMessage).styledChat_getOriginal(), signedMessage.getContent());
        ((ExtSignedMessage) (Object) signedMessage).styledChat_setArg("base_input", input);

        var config = ConfigManager.getConfig();
        for (var player : collection) {
            serverCommandSource.sendFeedback(config.getPrivateMessageSent(serverCommandSource.getDisplayName(), player.getDisplayName(), input, player.getCommandSource()), false);
        }
    }

    @Redirect(method = "method_44144", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_noopFeedback(ServerCommandSource instance, SentMessage message, boolean bl, MessageType.Parameters parameters) {
        // noop
    }

    @Redirect(method = "method_44144", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_formatText(ServerPlayerEntity instance, SentMessage message, boolean bl, MessageType.Parameters parameters, ServerCommandSource source) {
        try {
            ((ExtSignedMessage) (Object) ExtSentMessage.getWrapped(message)).styledChat_setArg("targets", instance.getDisplayName());
            StyledChatUtils.modifyForSending(ExtSentMessage.getWrapped(message), source, MessageType.MSG_COMMAND_INCOMING);
            instance.sendChatMessage(message, bl, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
