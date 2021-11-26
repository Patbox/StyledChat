package eu.pb4.styledchat.mixin;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {

    @Unique
    private static Text styledChat_sentFormatted = null;
    @Unique
    private static Text styledChat_receivedFormatted = null;

    @ModifyVariable(method = "execute", at = @At("HEAD"), index = 1)
    private static Text styledChat_formatText(Text message, ServerCommandSource source) {
        var config = ConfigManager.getConfig();
        if (config.configData.formattingInTeamMessages) {
            Map<String, TextParser.TextFormatterHandler> formatting;
            Map<String, Text> emotes;

            try {
                var player = source.getPlayer();
                formatting = StyledChatUtils.getHandlers(player);
                emotes = StyledChatUtils.getEmotes(player);
            } catch (Exception e) {
                formatting = TextParser.getRegisteredSafeTags();
                emotes = StyledChatUtils.getEmotes(source.getServer());
            }

            if (formatting.size() != 0) {
                var ogMessage = message.getString();
                var parsed =  PlaceholderAPI.parsePredefinedText(TextParser.parse(StyledChatUtils.formatMessage(ogMessage, formatting), formatting), StyledChatUtils.EMOTE_PATTERN, emotes); ;

                if (!ogMessage.equals(parsed.getString())) {
                    return parsed;
                }
            }
        }

        return message;
    }

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;fillStyle(Lnet/minecraft/text/Style;)Lnet/minecraft/text/MutableText;"))
    private static void styledChat_storeFormatted(ServerCommandSource source, Text message, CallbackInfoReturnable<Integer> cir) {
        var config = ConfigManager.getConfig();
        var team = ((Team) source.getEntity().getScoreboardTeam()).getFormattedName();
        team.setStyle(team.getStyle().withHoverEvent(null));

        styledChat_sentFormatted = config.getTeamChatSent(team, source.getDisplayName(), message, source);
        styledChat_receivedFormatted = config.getTeamChatReceived(team, source.getDisplayName(), message, source);
    }

    @Inject(method = "execute", at = @At("RETURN"))
    private static void styledChat_clearCache(ServerCommandSource source, Text message, CallbackInfoReturnable<Integer> cir) {
        styledChat_receivedFormatted = null;
        styledChat_sentFormatted = null;
    }

    @ModifyArg(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendSystemMessage(Lnet/minecraft/text/Text;Ljava/util/UUID;)V", ordinal = 0))
    private static Text styledChat_replaceText(Text _unused) {
        return styledChat_sentFormatted;
    }

    @ModifyArg(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendSystemMessage(Lnet/minecraft/text/Text;Ljava/util/UUID;)V", ordinal = 1))
    private static Text styledChat_replaceText2(Text _unused) {
        return styledChat_receivedFormatted;
    }
}
