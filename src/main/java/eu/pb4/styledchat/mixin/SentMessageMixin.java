package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.other.ExtendedSentMessage;
import eu.pb4.styledchat.other.StyledChatSentMessage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SentMessage.class)
public interface SentMessageMixin {
    @Inject(method = "of", at = @At("HEAD"), cancellable = true)
    private static void styledChat$patchStyle(SignedMessage message, CallbackInfoReturnable<SentMessage> cir) {
        var override = ((ExtSignedMessage) (Object) message).styledChat_getArg("override");
        if (override != StyledChatUtils.EMPTY_TEXT && StyledChatMod.server != null) {
            var type = ((ExtSignedMessage) (Object) message).styledChat_getType();

            if (message.isSenderMissing()) {
                cir.setReturnValue(new StyledChatSentMessage.System(message, override, StyledChatUtils.createParameters(override), type, new MutableObject<>()));
            } else {
                cir.setReturnValue(new StyledChatSentMessage.Chat(message, override, StyledChatUtils.createParameters(override), type, new MutableObject<>()));
            }
        }
    }

    @Inject(method = "of", at = @At("RETURN"), cancellable = true)
    private static void styledChat$forceAttach(SignedMessage message, CallbackInfoReturnable<SentMessage> cir) {
        if (cir.getReturnValue() instanceof SentMessage.Profileless) {
            ((ExtendedSentMessage) cir.getReturnValue()).styledChat$setMessage(message);
        }
    }

    @Mixin(SentMessage.Chat.class)
    class ChatMixin implements ExtendedSentMessage {

        @Shadow @Final private SignedMessage message;

        @Override
        public SignedMessage styledChat$message() {
            return this.message;
        }
    }

    @Mixin(SentMessage.Profileless.class)
    class ProfilelessMixin implements ExtendedSentMessage {
        @Unique
        private SignedMessage styledChat$message;

        @Override
        public SignedMessage styledChat$message() {
            return this.styledChat$message;
        }

        @Override
        public void styledChat$setMessage(SignedMessage value) {
            styledChat$message = value;
        }
    }
}
