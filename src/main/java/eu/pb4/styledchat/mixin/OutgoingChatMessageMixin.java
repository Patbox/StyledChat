package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtPlayerChatMessage;
import eu.pb4.styledchat.other.ExtendedSentMessage;
import eu.pb4.styledchat.other.StyledChatSentMessage;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OutgoingChatMessage.class)
public interface OutgoingChatMessageMixin {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void styledChat$patchStyle(PlayerChatMessage message, CallbackInfoReturnable<OutgoingChatMessage> cir) {
        var override = ((ExtPlayerChatMessage) (Object) message).styledChat_getArg("override");
        if (override != StyledChatUtils.EMPTY_TEXT && StyledChatMod.server != null) {
            var type = ((ExtPlayerChatMessage) (Object) message).styledChat_getType();

            if (message.isSystem()) {
                cir.setReturnValue(new StyledChatSentMessage.System(message, override, StyledChatUtils.createParameters(override), type, new MutableObject<>()));
            } else {
                cir.setReturnValue(new StyledChatSentMessage.Chat(message, override, StyledChatUtils.createParameters(override), type, new MutableObject<>()));
            }
        }
    }

    @Inject(method = "create", at = @At("RETURN"), cancellable = true)
    private static void styledChat$forceAttach(PlayerChatMessage message, CallbackInfoReturnable<OutgoingChatMessage> cir) {
        if (cir.getReturnValue() instanceof OutgoingChatMessage.Disguised) {
            ((ExtendedSentMessage) cir.getReturnValue()).styledChat$setMessage(message);
        }
    }

    @Mixin(OutgoingChatMessage.Player.class)
    class PlayerMixin implements ExtendedSentMessage {

        @Shadow @Final private PlayerChatMessage message;

        @Override
        public PlayerChatMessage styledChat$message() {
            return this.message;
        }
    }

    @Mixin(OutgoingChatMessage.Disguised.class)
    class DisguisedMixin implements ExtendedSentMessage {
        @Unique
        private PlayerChatMessage styledChat$message;

        @Override
        public PlayerChatMessage styledChat$message() {
            return this.styledChat$message;
        }

        @Override
        public void styledChat$setMessage(PlayerChatMessage value) {
            styledChat$message = value;
        }
    }
}
