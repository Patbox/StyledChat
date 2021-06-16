package eu.pb4.styledchat.mixin;


import eu.pb4.placeholders.TextParser;
import eu.pb4.placeholders.util.GeneralUtils;
import eu.pb4.styledchat.config.Config;
import eu.pb4.styledchat.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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


        for (Map.Entry<String, TextParser.TextFormatterHandler> entry : TextParser.getRegisteredTags().entrySet()) {
            if (Permissions.check(source, "styledchat.format." + entry.getKey(), 2)
                    || Permissions.check(source, "styledchat.format." + entry.getKey(), config.defaultFormattingCodes.getBoolean(entry.getKey()))) {
                handlers.put(entry.getKey(), entry.getValue());
            }
        }

        if (Permissions.check(source, "styledchat.format.item", 2)
                || Permissions.check(source, "styledchat.format.item", config.defaultFormattingCodes.getBoolean("item"))) {
            handlers.put("item", (tag, data, input, buildInHandlers, endAt) -> new GeneralUtils.TextLengthPair((MutableText) player.getStackInHand(Hand.MAIN_HAND).toHoverableText(), 0));
        }

        String rawMessage = message.getRaw();
        String filteredMessage = message.getRaw();

        if (config.configData.legacyChatFormatting) {
            for (Formatting formatting : Formatting.values()) {
                if (handlers.get(formatting.getName()) != null) {
                    rawMessage = rawMessage.replaceAll(String.copyValueOf(new char[] {'&', formatting.getCode()}), "<" + formatting.getName() + ">");
                    filteredMessage = filteredMessage.replaceAll(String.copyValueOf(new char[] {'&', formatting.getCode()}), "<" + formatting.getName() + ">");
                }
            }
        }

        Text rawText = config.getChat(this.player, handlers.size() > 0 ? TextParser.parse(rawMessage, handlers) : new LiteralText(message.getRaw()));
        Text filteredText = config.getChat(this.player, handlers.size() > 0 ? TextParser.parse(filteredMessage, handlers) : new LiteralText(message.getFiltered()));

        playerManager.broadcast(rawText, (player) -> this.player.shouldFilterMessagesSentTo(player) ?  filteredText : rawText, playerMessageType, sender);
    }
}
