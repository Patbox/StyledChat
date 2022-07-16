package eu.pb4.styledchat.mixin.commands;

import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(method = "method_44144", at = @At("HEAD"))
    private static void styledChat_formatOgText(MessageSourceProfile messageSourceProfile, Collection<ServerPlayerEntity> collection, ServerCommandSource serverCommandSource, MessageType.Parameters parameters, FilteredMessage<SignedMessage> filteredMessage, CallbackInfo ci) {
        var input = StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) filteredMessage.raw()).styledChat_getOriginal());
        ((ExtSignedMessage) (Object) filteredMessage.raw()).styledChat_setArg("base_input", input);

        if (filteredMessage.raw() != filteredMessage.filtered()) {
            ((ExtSignedMessage) (Object) filteredMessage.filtered()).styledChat_setArg("base_input", StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) filteredMessage.filtered()).styledChat_getOriginal()));
        }

        var config = ConfigManager.getConfig();
        for (var player : collection) {
            serverCommandSource.sendFeedback(config.getPrivateMessageSent(serverCommandSource.getDisplayName(), player.getDisplayName(), input, player.getCommandSource()), false);
        }
    }

    @Redirect(method = "method_44144", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/ServerCommandSource;sendChatMessage(Lnet/minecraft/network/message/SentMessage;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_noopFeedback(ServerCommandSource instance, SentMessage message, MessageType.Parameters params) {
        // noop
    }

    @Redirect(method = "method_44144", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/filter/FilteredMessage;getFilterableFor(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/server/network/ServerPlayerEntity;)Ljava/lang/Object;"))
    private static Object styledChat_formatText(FilteredMessage<SentMessage> instance, ServerCommandSource source, ServerPlayerEntity receiver) {
        try {
            var x = instance.getFilterableFor(source, receiver);
            ((ExtSignedMessage) (Object) x.getWrappedMessage()).styledChat_setArg("targets", receiver.getDisplayName());
            StyledChatUtils.modifyForSending(x.getWrappedMessage(), source, MessageType.MSG_COMMAND_INCOMING);
            return x;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
