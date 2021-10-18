package eu.pb4.styledchat.mixin;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(MeCommand.class)
public class MeCommandMixin {
    @Inject(method = "getEmoteText", at = @At("HEAD"), cancellable = true)
    private static void styledChat_formatText(CommandContext<ServerCommandSource> context, String arg, CallbackInfoReturnable<Text> cir) {
        var config = ConfigManager.getConfig();
        var source = context.getSource();

        Text message;
        Map<String, TextParser.TextFormatterHandler> formatting;

        try {
            var player = source.getPlayer();
            formatting = StyledChatUtils.getHandlers(player);
        } catch (Exception e) {
            formatting = TextParser.getRegisteredSafeTags();
        }
        var emotes = config.getEmotes(source);


        if (formatting.size() != 0) {
            var formattedMessage = StyledChatUtils.formatMessage(arg, formatting);

            message = TextParser.parse(formattedMessage, formatting);
        } else {
            message = new LiteralText(arg);
        }

        if (emotes.size() != 0) {
            message = PlaceholderAPI.parsePredefinedText(message, StyledChatUtils.EMOTE_PATTERN, emotes);
        }


        cir.setReturnValue(config.getMeCommand(source, message));
    }
}
