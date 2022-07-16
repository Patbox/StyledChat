package eu.pb4.styledchat.mixin;


import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(MessageArgumentType.SignedMessage.class)
public class MessageArgumentTypeSignedMessageMixin {
    @Redirect(method = "decorate(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/filter/FilteredMessage;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getMessageDecorator()Lnet/minecraft/network/message/MessageDecorator;"))
    private MessageDecorator styledChat_returnCached(MinecraftServer instance) {
        return (player, message) -> {
            if (player != null) {
                var cached = ((ExtPlayNetworkHandler) player.networkHandler).styledChat_getLastCached();
                return CompletableFuture.completedFuture(cached != null ? cached : message);
            }
            return CompletableFuture.completedFuture(message);
        };
    }

    /*@Redirect(method = "decorate", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenAccept(Ljava/util/function/Consumer;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> styledChat_replaceFormatting(CompletableFuture<FilteredMessage<SignedMessage>> instance, Consumer<FilteredMessage<SignedMessage>> action) {
        return instance.thenAccept((x) -> {
            ((ExtSignedMessage) (Object) x.raw()).styledChat_setOriginal(this.plain);

            if (x.raw() != x.filtered()) {
                ((ExtSignedMessage) (Object) x.filtered()).styledChat_setOriginal(this.plain);
            }
            action.accept(x);
        });
    }*/
}
