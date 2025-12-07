package eu.pb4.styledchat.mixin.commands;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtPlayerChatMessage;
import eu.pb4.styledchat.other.ExtendedSentMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(method = "sendMessage", at = @At(value = "HEAD"))
    private static void styledChat_formatOgText(CommandSourceStack serverCommandSource, Entity entity, PlayerTeam team, List<ServerPlayer> list, PlayerChatMessage signedMessage, CallbackInfo ci) {
        var input = ExtPlayerChatMessage.getArg(signedMessage, "base_input");

        if (input == StyledChatUtils.EMPTY_TEXT) {
            input = StyledChatUtils.formatFor(serverCommandSource, ((ExtPlayerChatMessage) (Object) signedMessage).styledChat_getOriginal());
            ExtPlayerChatMessage.setArg(signedMessage,"base_input", input);
        }
    }

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;sendChatMessage(Lnet/minecraft/network/chat/OutgoingChatMessage;ZLnet/minecraft/network/chat/ChatType$Bound;)V"))
    private static void styledChat_replaceForSelf(ServerPlayer instance, OutgoingChatMessage message, boolean bl, ChatType.Bound parameters, CommandSourceStack source) {
        if (message instanceof ExtendedSentMessage extSentMessage) {
            try {
                if (source.getPlayer() == instance) {
                    var sent = ChatType.bind(StyledChatMod.MESSAGE_TYPE_ID, source.getServer().registryAccess(), StyledChatStyles.getTeamChatSent(
                        source.getEntity().getTeam().getFormattedDisplayName(),
                        source.getDisplayName(),
                        ExtPlayerChatMessage.getArg(extSentMessage.styledChat$message(), "base_input"), instance.createCommandSourceStack()
                    ));

                    source.sendChatMessage(message, bl, sent);
                } else {
                    var rex = ChatType.bind(StyledChatMod.MESSAGE_TYPE_ID, source.getServer().registryAccess(), StyledChatStyles.getTeamChatReceived(
                            source.getEntity().getTeam().getFormattedDisplayName(),
                            source.getDisplayName(),
                            ExtPlayerChatMessage.getArg(extSentMessage.styledChat$message(), "base_input"), source
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
