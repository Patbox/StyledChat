package eu.pb4.styledchat.mixin.commands;

import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(method = "method_44148", at = @At(value = "HEAD"))
    private static void styledChat_formatOgText(MessageSourceProfile messageSourceProfile, List list, Entity entity, MessageType.Parameters parameters, ServerCommandSource serverCommandSource, MessageType.Parameters parameters2, FilteredMessage<SentMessage> decoratedMessage, CallbackInfo ci) {
        var input = StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) decoratedMessage.raw().getWrappedMessage()).styledChat_getOriginal());
        ((ExtSignedMessage) (Object) decoratedMessage.raw().getWrappedMessage()).styledChat_setArg("base_input", input);
        StyledChatUtils.modifyForSending(decoratedMessage.raw().getWrappedMessage(), serverCommandSource, MessageType.TEAM_MSG_COMMAND_OUTGOING);

        if (decoratedMessage.raw().getWrappedMessage() != decoratedMessage.filtered().getWrappedMessage()) {
            ((ExtSignedMessage) (Object) decoratedMessage.filtered().getWrappedMessage()).styledChat_setArg("base_input", StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) decoratedMessage.filtered().getWrappedMessage()).styledChat_getOriginal()));
            StyledChatUtils.modifyForSending(decoratedMessage.raw().getWrappedMessage(), serverCommandSource, MessageType.TEAM_MSG_COMMAND_OUTGOING);
        }

        var config = ConfigManager.getConfig();
        serverCommandSource.sendFeedback(config.getTeamChatSent(((Team) entity.getScoreboardTeam()).getFormattedName(), entity.getDisplayName(), input, serverCommandSource), false);
    }

    @Redirect(method = "method_44148", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;Lnet/minecraft/network/message/MessageType$Parameters;)V", ordinal = 0))
    private static void styledChat_noop(ServerPlayerEntity instance, SentMessage message, MessageType.Parameters params) {}
}
