package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkManagerMixin implements ExtPlayNetworkHandler {

    @Unique
    Text styledChat_lastCached = null;

    @Shadow
    public ServerPlayerEntity player;

    @ModifyArg(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private Text styledChat_replaceDisconnectMessage(Text text) {
        return ConfigManager.getConfig().getLeft(this.player);
    }

    @Redirect(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getMessageDecorator()Lnet/minecraft/network/message/MessageDecorator;"))
    private MessageDecorator styledChat_replaceDecorator(MinecraftServer instance) {
        return (player, message) -> CompletableFuture.completedFuture(this.styledChat_lastCached != null ? this.styledChat_lastCached : message);
    }

    @Inject(method = "handleMessage", at = @At("HEAD"))
    private void styledChat_removeCachedIfNotPreviewed(ChatMessageC2SPacket packet, FilteredMessage<String> message, CallbackInfo ci) {
       if (!packet.isPreviewed()) {
           this.styledChat_lastCached = null;
       }
    }

    @Redirect(method = "decorateChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getMessageDecorator()Lnet/minecraft/network/message/MessageDecorator;"))
    private MessageDecorator styledChat_replaceDecorator2(MinecraftServer instance) {
        return StyledChatUtils.getChatDecorator();
    }

    @Inject(method = "sendChatPreviewPacket", at = @At("HEAD"))
    private void styledChat_store(int queryId, Text preview, CallbackInfo ci) {
        this.styledChat_lastCached = preview;
    }

    @Inject(method = "handleDecoratedMessage", at = @At("HEAD"))
    private void styledChat_setFormattedMessage(FilteredMessage<SignedMessage> message, CallbackInfo ci) {
        StyledChatUtils.modifyForSending(message, this.player.getCommandSource(), MessageType.CHAT);
    }

    @Override
    public @Nullable Text styledChat_getLastCached() {
        return this.styledChat_lastCached;
    }
}
