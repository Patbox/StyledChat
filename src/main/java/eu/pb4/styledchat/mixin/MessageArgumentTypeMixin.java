package eu.pb4.styledchat.mixin;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.styledchat.ducks.ExtMessageFormat;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;


@Mixin(MessageArgumentType.class)
public class MessageArgumentTypeMixin {

    @ModifyArg(method = "getSignedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/argument/MessageArgumentType;chain(Ljava/util/function/Consumer;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/network/message/SignedMessage;)V"), index = 2)
    private static SignedMessage styledChat_attachSource(Consumer<SignedMessage> callback, ServerCommandSource source, SignedMessage message) {
        ExtSignedMessage.of(message).styledChat_setSource(source);
        return message;
    }

    @ModifyArg(method = "getSignedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/argument/MessageArgumentType;chainUnsigned(Ljava/util/function/Consumer;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/network/message/SignedMessage;)V"), index = 2)
    private static SignedMessage styledChat_attachSource2(Consumer<SignedMessage> callback, ServerCommandSource source, SignedMessage message) {
        ExtSignedMessage.of(message).styledChat_setSource(source);
        return message;
    }
}
