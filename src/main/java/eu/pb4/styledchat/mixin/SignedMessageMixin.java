package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(SignedMessage.class)
public class SignedMessageMixin implements ExtSignedMessage {
    @Unique
    private String styledChat_original;

    @Unique
    private Map<String, Text> styledChat_args = new HashMap<>();

    @Override
    public void styledChat_setOriginal(String message) {
        this.styledChat_original = message;
    }

    @Override
    public void styledChat_setArg(String name, Text arg) {
        this.styledChat_args.put(name, arg);
    }

    @Override
    public String styledChat_getOriginal() {
        return this.styledChat_original;
    }

    @Override
    public Text styledChat_getArg(String name) {
        return this.styledChat_args.getOrDefault(name, StyledChatUtils.EMPTY_TEXT);
    }

    @Inject(method = "of(Lnet/minecraft/text/Text;Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignature;Z)Lnet/minecraft/network/message/SignedMessage;", at = @At("RETURN"))
    private static void styledChat_setOriginal(Text originalContent, Text decoratedContent, MessageSignature signature, boolean previewed, CallbackInfoReturnable<SignedMessage> cir) {
        ((ExtSignedMessage) (Object) cir.getReturnValue()).styledChat_setOriginal(originalContent.getString());
    }
}
