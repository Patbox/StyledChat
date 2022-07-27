package eu.pb4.styledchat.mixin.commands;

import eu.pb4.styledchat.ducks.ExtSentMessage;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(method = "method_44148", at = @At(value = "HEAD"))
    private static void styledChat_formatOgText(List list, Entity entity, MessageType.Parameters parameters, MessageType.Parameters parameters2, ServerCommandSource serverCommandSource, SignedMessage signedMessage, CallbackInfo ci) {
        var input = ((ExtSignedMessage) (Object) signedMessage).styledChat_getArg("base_input");

        if (input == null) {
            input = StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) signedMessage).styledChat_getOriginal());
            ((ExtSignedMessage) (Object) signedMessage).styledChat_setArg("base_input", input);
        }

        StyledChatUtils.modifyForSending(signedMessage, serverCommandSource, MessageType.TEAM_MSG_COMMAND_INCOMING);

        var config = ConfigManager.getConfig();
        serverCommandSource.sendFeedback(config.getTeamChatSent(((Team) entity.getScoreboardTeam()).getFormattedName(), entity.getDisplayName(), input, serverCommandSource), false);
    }

    @Redirect(method = "method_44148", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_replaceForSelf(ServerPlayerEntity instance, SentMessage message, boolean bl, MessageType.Parameters parameters) {
        if (!ExtSentMessage.getWrapped(message).canVerifyFrom(instance.getUuid())) {
            instance.sendChatMessage(message, bl, parameters);
        }
    }
}
