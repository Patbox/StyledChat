package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.other.StyledChatSentMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SentMessage.class)
public interface SentMessageMixin {
    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void styledChat$patchStyle(SignedMessage message, CallbackInfoReturnable<SentMessage> cir) {
        var override = ((ExtSignedMessage) (Object) message).styledChat_getArg("override");
        if (override != null && StyledChatMod.server != null) {
            if (message.method_46293()) {
                cir.setReturnValue(new StyledChatSentMessage.System(message, override, new MessageType.Parameters(StyledChatMod.getMessageType(), override, null)));
            } else {
                cir.setReturnValue(new StyledChatSentMessage.Chat(message, override, new MessageType.Parameters(StyledChatMod.getMessageType(), override, null)));
            }
        }
    }
}
