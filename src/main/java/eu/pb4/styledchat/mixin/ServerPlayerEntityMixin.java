package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
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
    @Shadow @Final public MinecraftServer server;

    @Shadow public abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Shadow public abstract void playerTick();

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

    /*@Redirect(method = "sendChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void styledChat_hacky(ServerPlayNetworkHandler instance, Packet<?> packet, SignedMessage message, MessageSender sender, RegistryKey<MessageType> typeKey) {
        var override = ((ExtSignedMessage) (Object) message).styledChat_getArg("override");
        if (override != null) {
            instance.sendPacket(new ChatMessageS2CPacket(message.signedContent(), Optional.empty(),
                    this.getMessageTypeId(StyledChatMod.MESSAGE_TYPE), new MessageSender(sender.profileId(), override),
                    message.signature().timestamp(), message.signature().saltSignature()));

        } else {
            instance.sendPacket(packet);
        }
    }*/

    /*@Redirect(method = "sendChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/SentMessage;toPacket(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)Lnet/minecraft/network/packet/s2c/play/ChatMessageS2CPacket;"))
    private ChatMessageS2CPacket styledChat_hacky(SentMessage instance, ServerPlayerEntity player, MessageType.Parameters parameters) {
        //var message = arg.method_44852();
        var reg = this.server.getRegistryManager().get(Registry.MESSAGE_TYPE_KEY);
        var override = ((ExtSignedMessage) (Object) instance.getWrappedMessage()).styledChat_getArg("override");
        if (override != null) {
            /*nstance.sendPacket(new ChatMessageS2CPacket(message, new MessageType.class_7603(
                    reg.getRawId(reg.get(StyledChatMod.MESSAGE_TYPE)), override, null
            )));* /

            return null;
        } else {
            return instance.toPacket(player, parameters);
        }
    }*/
}
