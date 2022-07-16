package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SentMessage.Chat.class)
public abstract class SentMessageChatMixin {
    @Shadow public abstract SignedMessage getWrappedMessage();

    @Redirect(method = "toPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageType$Parameters;toSerialized(Lnet/minecraft/util/registry/DynamicRegistryManager;)Lnet/minecraft/network/message/MessageType$Serialized;"))
    private MessageType.Serialized styledChat_replaceSerialized(MessageType.Parameters instance, DynamicRegistryManager registryManager) {
        var override = ((ExtSignedMessage) (Object) this.getWrappedMessage()).styledChat_getArg("override");
        if (override != null) {
            var reg = registryManager.get(Registry.MESSAGE_TYPE_KEY);
            return new MessageType.Serialized(reg.getRawId(reg.get(StyledChatMod.MESSAGE_TYPE_ID)), override, null);
        } else {
            return instance.toSerialized(registryManager);
        }
    }
}
