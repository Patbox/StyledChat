package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
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
    @Inject(method = "method_44148", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessage(Lnet/minecraft/text/Text;)V"))
    private static void styledChat_formatOgText(List list, Entity entity, Text text, ServerCommandSource serverCommandSource, MessageSender messageSender, FilteredMessage<SignedMessage> decoratedMessage, CallbackInfo ci) {
        var input = StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) decoratedMessage.raw()).styledChat_getOriginal());
        ((ExtSignedMessage) (Object) decoratedMessage.raw()).styledChat_setArg("base_input", input);

        if (decoratedMessage.raw() != decoratedMessage.filtered()) {
            ((ExtSignedMessage) (Object) decoratedMessage.filtered()).styledChat_setArg("base_input", StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) decoratedMessage.filtered()).styledChat_getOriginal()));
        }

        var config = ConfigManager.getConfig();
        serverCommandSource.sendFeedback(config.getTeamChatSent(((Team) entity.getScoreboardTeam()).getFormattedName(), entity.getDisplayName(), input, serverCommandSource), false);

        StyledChatUtils.modifyForSending(decoratedMessage, serverCommandSource, MessageType.TEAM_MSG_COMMAND);
    }

    @Redirect(method = "method_44148", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessage(Lnet/minecraft/text/Text;)V"))
    private static void styledChat_noop(ServerPlayerEntity instance, Text message) {

    }
}
