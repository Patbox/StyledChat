package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.network.Packet;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow protected abstract int getMessageTypeId(RegistryKey<MessageType> typeKey);

    @Shadow @Final public MinecraftServer server;

    @ModifyArg(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private Text styledChat_replaceDeathMessage(Text text) {
        return ConfigManager.getConfig().getDeath((ServerPlayerEntity) (Object) this, text);
    }

    @Inject(method = "sendMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(Text message, CallbackInfo ci) {
        if (message == StyledChatUtils.IGNORED_TEXT) {
            ci.cancel();
        }
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(SignedMessage message, MessageSender sender, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
        if (message.getContent() == StyledChatUtils.IGNORED_TEXT) {
            ci.cancel();
        }
    }

    @Redirect(method = "sendChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void styledChat_hacky(ServerPlayNetworkHandler instance, Packet<?> packet, SignedMessage message, MessageSender sender, RegistryKey<MessageType> typeKey) {
        var override = ((ExtSignedMessage) (Object) message).styledChat_getArg("override");
        if (override != null) {
            instance.sendPacket(new ChatMessageS2CPacket(message.signedContent(), message.unsignedContent(),
                    this.getMessageTypeId(StyledChatMod.MESSAGE_TYPE), new MessageSender(sender.profileId(), override),
                    message.signature().timestamp(), message.signature().saltSignature()));

        } else {
            instance.sendPacket(packet);
        }
    }
}
