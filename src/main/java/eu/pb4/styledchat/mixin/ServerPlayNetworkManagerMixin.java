package eu.pb4.styledchat.mixin;


import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;

import eu.pb4.styledchat.StyledChatEvents;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.Config;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Function;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkManagerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @ModifyArg(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private Text styledChat_replaceDisconnectMessage(Text text) {
        return ConfigManager.getConfig().getLeft(this.player);
    }

    @Redirect(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void styledChat_replaceChatMessage(PlayerManager playerManager, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType playerMessageType, UUID sender, TextStream.Message message) {
        var handlers = StyledChatUtils.getHandlers(this.player);
        Config config = ConfigManager.getConfig();
        var emotes = StyledChatUtils.getEmotes(this.player);

        String rawMessage =  message.getRaw();
        String filteredMessage = message.getFiltered();


        // You might say, that it's useless and you would be kinda right
        // However in case of other mods or vanilla implementing these, it should work without any modifications!
        if (rawMessage.equals(filteredMessage)) {
            rawMessage = StyledChatEvents.PRE_MESSAGE_CONTENT_SEND.invoker().onPreMessage(message.getRaw(), player, false);

            rawMessage = StyledChatUtils.formatMessage(rawMessage, handlers);
            Text rawText = config.getChat(this.player,
                    StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                    ? PlaceholderAPI.parsePredefinedText(TextParser.parse(rawMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                    : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getRaw()), StyledChatUtils.EMOTE_PATTERN, emotes),
                            player, false)
            );

            if (rawText != null ) {
                playerManager.broadcast(rawText, (receiver) -> StyledChatEvents.MESSAGE_TO_SEND.invoker().onMessageTo(rawText, this.player, receiver, false), playerMessageType, sender);
            }
        } else {
            rawMessage = StyledChatEvents.PRE_MESSAGE_CONTENT_SEND.invoker().onPreMessage(message.getRaw(), player, false);
            filteredMessage = StyledChatEvents.PRE_MESSAGE_CONTENT_SEND.invoker().onPreMessage(message.getFiltered(), player, true);

            rawMessage = StyledChatUtils.formatMessage(rawMessage, handlers);
            filteredMessage = StyledChatUtils.formatMessage(filteredMessage, handlers);

            Text rawText = config.getChat(this.player,
                    StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                    ? PlaceholderAPI.parsePredefinedText(TextParser.parse(rawMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                    : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getRaw()), StyledChatUtils.EMOTE_PATTERN, emotes),
                            player, false)
            );
            Text filteredText = config.getChat(this.player,
                    StyledChatEvents.MESSAGE_CONTENT_SEND.invoker().onMessage(handlers.size() > 0
                                    ? PlaceholderAPI.parsePredefinedText(TextParser.parse(filteredMessage, handlers), StyledChatUtils.EMOTE_PATTERN, emotes)
                                    : PlaceholderAPI.parsePredefinedText(new LiteralText(message.getFiltered()), StyledChatUtils.EMOTE_PATTERN, emotes),
                            player, true)
            );

            if (rawText != null && filteredText != null) {
                playerManager.broadcast(rawText, (receiver) -> {
                    var filtered = this.player.shouldFilterMessagesSentTo(receiver);
                    return StyledChatEvents.MESSAGE_TO_SEND.invoker().onMessageTo(filtered ? filteredText : rawText, this.player, receiver, filtered);

                }, playerMessageType, sender);
            }
        }
    }
}
