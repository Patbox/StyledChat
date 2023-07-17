package eu.pb4.styledchat.mixin.commands;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.other.ExtendedSentMessage;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(method = "execute", at = @At(value = "HEAD"))
    private static void styledChat_formatOgText(ServerCommandSource serverCommandSource, Entity entity, Team team, List<ServerPlayerEntity> list, SignedMessage signedMessage, CallbackInfo ci) {
        var input = ExtSignedMessage.getArg(signedMessage, "base_input");

        if (input == StyledChatUtils.EMPTY_TEXT) {
            input = StyledChatUtils.formatFor(serverCommandSource, ((ExtSignedMessage) (Object) signedMessage).styledChat_getOriginal());
            ExtSignedMessage.setArg(signedMessage,"base_input", input);
        }
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V"))
    private static void styledChat_replaceForSelf(ServerPlayerEntity instance, SentMessage message, boolean bl, MessageType.Parameters parameters, ServerCommandSource source) {
        if (message instanceof ExtendedSentMessage extSentMessage) {
            try {
                if (source.getPlayer() == instance) {
                    var sent = StyledChatMod.getMessageType().params(StyledChatStyles.getTeamChatSent(
                            ((Team) source.getEntity().getScoreboardTeam()).getFormattedName(),
                            source.getDisplayName(),
                            ExtSignedMessage.getArg(extSentMessage.styledChat$message(), "base_input"), instance.getCommandSource()
                    ));

                    source.sendChatMessage(message, bl, sent);
                } else {
                    var rex = StyledChatMod.getMessageType().params(StyledChatStyles.getTeamChatReceived(
                            ((Team) source.getEntity().getScoreboardTeam()).getFormattedName(),
                            source.getDisplayName(),
                            ExtSignedMessage.getArg(extSentMessage.styledChat$message(), "base_input"), source
                    ));

                    instance.sendChatMessage(message, bl, rex);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
