package eu.pb4.styledchat.mixin;


import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(MessageArgumentType.SignedMessage.class)
public class MessageArgumentTypeSignedMessageMixin {
    @Redirect(method = "method_44266", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getMessageDecorator()Lnet/minecraft/network/message/MessageDecorator;"))
    private MessageDecorator styledChat_returnCached(MinecraftServer instance) {
        return (player, message) -> {
            var cached = ((ExtPlayNetworkHandler) player.networkHandler).styledChat_getLastCached();
            return CompletableFuture.completedFuture(cached != null ? cached : message);
        };
    }
}
