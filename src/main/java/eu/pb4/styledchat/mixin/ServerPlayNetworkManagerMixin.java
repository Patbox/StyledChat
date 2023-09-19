package eu.pb4.styledchat.mixin;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.config.ChatStyle;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkManagerMixin implements ExtPlayNetworkHandler {

    @Shadow
    public ServerPlayerEntity player;

    @Unique
    private ChatStyle styledChat$style;

    @ModifyArg(method = "cleanUp", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private Text styledChat_replaceDisconnectMessage(Text text) {
        return StyledChatStyles.getLeft(this.player);
    }

    @Redirect(method = "method_44900", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageDecorator;decorate(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/text/Text;)Lnet/minecraft/text/Text;"))
    private Text styledChat_replaceDecorator2(MessageDecorator instance, ServerPlayerEntity player, Text text) {
        if (player != null) {
            return StyledChatUtils.formatFor(PlaceholderContext.of(player), text.getString());
        } else {
            return StyledChatUtils.formatFor(PlaceholderContext.of(StyledChatMod.server), text.getString());
        }
    }

    @Inject(method = "handleDecoratedMessage", at = @At("HEAD"))
    private void styledChat_setFormattedMessage(SignedMessage signedMessage, CallbackInfo ci) {
        StyledChatUtils.modifyForSending(signedMessage, this.player.getCommandSource(), MessageType.CHAT);
    }

    @Override
    public ChatStyle styledChat$getStyle() {
        if (this.styledChat$style == null) {
            this.styledChat$style = StyledChatUtils.createStyleOf(this.player);
        }
        return this.styledChat$style;
    }

    @Override
    public void styledChat$setStyle(ChatStyle style) {
        this.styledChat$style = style;
    }

    @Override
    public boolean styledChat$chatColors() {
        return this.player.areClientChatColorsEnabled() || !ConfigManager.getConfig().configData.formatting.respectColors;
    }
}
