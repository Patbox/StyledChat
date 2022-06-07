package eu.pb4.styledchat.mixin;

import net.minecraft.network.message.MessageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(MessageType.class)
public class MessageTypeMixin {
    @ModifyArg(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/message/MessageType;<init>(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;)V"), index = 0)
    private static Optional styledChat_replace(Optional<MessageType.DisplayRule> optional) {
        return optional.isPresent() && optional.get().decoration().isPresent() ? Optional.of(MessageType.DisplayRule.of()) : optional;
    }
}
