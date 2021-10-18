package eu.pb4.styledchat.mixin;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @ModifyVariable(method = "execute", at = @At("HEAD"), index = 2)
    private static Text styledChat_formatText(Text message, ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        var config = ConfigManager.getConfig();
            if (config.configData.formattingInPrivateMessages) {
                Map<String, TextParser.TextFormatterHandler> formatting;

                try {
                    var player = source.getPlayer();
                    formatting = StyledChatUtils.getHandlers(player);
                } catch (Exception e) {
                    formatting = TextParser.getRegisteredSafeTags();
                }
                var emotes = config.getEmotes(source);

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

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private static <T> void styledChat_redirectSending(Consumer consumer, T t, ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) {
        var entity = source.getEntity();

        var receiver = (Text) t;
        var output = ConfigManager.getConfig().getPrivateMessageSent(source.getDisplayName(), receiver, message, source);

        if (entity instanceof ServerPlayerEntity player) {
            player.sendSystemMessage(output, player.getUuid());
        } else {
            source.sendFeedback(output, false);
        }
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendSystemMessage(Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private static <T> void styledChat_redirectReceiving(ServerPlayerEntity receiver, Text _ignored, UUID sender, ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) {
        var output = ConfigManager.getConfig().getPrivateMessageReceived(source.getDisplayName(), receiver.getDisplayName(), message, source);
        receiver.sendSystemMessage(output, sender);
    }
}
