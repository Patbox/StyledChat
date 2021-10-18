package eu.pb4.styledchat.mixin;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SayCommand.class)
public class SayCommandMixin {
    @ModifyVariable(method = "method_13563", at = @At("STORE"), ordinal = 1)
    private static Text styledChat_formatText(Text inputX, CommandContext<ServerCommandSource> context) {
        var input = (Text) ((TranslatableText) inputX).getArgs()[1];

        var inputAsString = input.getString();

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
            var formattedMessage = StyledChatUtils.formatMessage(inputAsString, formatting);
            var tmpMessage = TextParser.parse(formattedMessage, formatting);

            if (!tmpMessage.getString().equals(inputAsString)) {
                message = tmpMessage;
            } else {
                message = input;
            }
        } else {
            message = input;
        }

        if (emotes.size() != 0) {
            message = PlaceholderAPI.parsePredefinedText(message, StyledChatUtils.EMOTE_PATTERN, emotes);
        }

        return config.getSayCommand(source, message);
    }
}
