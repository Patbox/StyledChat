package eu.pb4.styledchat.mixin;


import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import eu.pb4.placeholders.util.GeneralUtils;
import eu.pb4.placeholders.util.TextParserUtils;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.config.Config;
import eu.pb4.styledchat.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkManagerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @ModifyArg(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private Text replaceDisconnectMessage(Text text) {
        return ConfigManager.getConfig().getLeft(this.player);
    }

    @Redirect(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void replaceChatMessage(PlayerManager playerManager, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType playerMessageType, UUID sender, TextStream.Message message) {
        HashMap<String, TextParser.TextFormatterHandler> handlers = new HashMap<>();
        ServerCommandSource source = this.player.getCommandSource();
        Config config = ConfigManager.getConfig();

        String rawMessage = message.getRaw();
        String filteredMessage = message.getRaw();

        for (Map.Entry<String, TextParser.TextFormatterHandler> entry : TextParser.getRegisteredTags().entrySet()) {
            if (!entry.getKey().equals("click")
                    && config.defaultFormattingCodes.getBoolean(entry.getKey())
                    || Permissions.check(source, "styledchat.format." + entry.getKey(), 2)) {
                handlers.put(entry.getKey(), entry.getValue());
            }
        }

        if (config.defaultFormattingCodes.getBoolean("item") ||
                Permissions.check(source, "styledchat.format.item",  2)) {
            handlers.put("item", (tag, data, input, buildInHandlers, endAt) -> new GeneralUtils.TextLengthPair((MutableText) player.getStackInHand(Hand.MAIN_HAND).toHoverableText(), 0));
        }

        if (config.defaultFormattingCodes.getBoolean("pos") ||
                Permissions.check(source, "styledchat.format.pos",  2)) {
            handlers.put("pos", (tag, data, input, buildInHandlers, endAt) -> new GeneralUtils.TextLengthPair(new LiteralText(player.getBlockPos().toShortString()), 0));
        }

        if (config.configData.parseLinksInChat
                || Permissions.check(source, "styledchat.links",  2)) {
            handlers.put("sc-link", (tag, data, input, buildInHandlers, endAt) -> {
                String url = TextParserUtils.cleanArgument(data);
                return new GeneralUtils.TextLengthPair(
                        (MutableText) PlaceholderAPI.parsePredefinedText(
                                config.linkStyle,
                                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                                Map.of("link", new LiteralText(url).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))))
                        ), 0);
            });


            rawMessage = rawMessage.replaceAll(StyledChatMod.URL_REGEX, "<sc-link:'$1'>");
            filteredMessage = filteredMessage.replaceAll(StyledChatMod.URL_REGEX, "<sc-link:'$1'>");
        }

        if (config.configData.legacyChatFormatting) {
            for (Formatting formatting : Formatting.values()) {
                if (handlers.get(formatting.getName()) != null) {
                    rawMessage = rawMessage.replace(String.copyValueOf(new char[] {'&', formatting.getCode()}), "<" + formatting.getName() + ">");
                    filteredMessage = filteredMessage.replace(String.copyValueOf(new char[] {'&', formatting.getCode()}), "<" + formatting.getName() + ">");
                }
            }
        }

        Text rawText = config.getChat(this.player, handlers.size() > 0 ? TextParser.parse(rawMessage, handlers) : new LiteralText(message.getRaw()));
        Text filteredText = config.getChat(this.player, handlers.size() > 0 ? TextParser.parse(filteredMessage, handlers) : new LiteralText(message.getFiltered()));

        if (rawText != null && filteredText != null) {
            playerManager.broadcast(rawText, (player) -> this.player.shouldFilterMessagesSentTo(player) ? filteredText : rawText, playerMessageType, sender);
        }
    }
}
