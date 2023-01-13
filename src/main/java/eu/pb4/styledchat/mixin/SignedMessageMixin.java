package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.network.message.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(SignedMessage.class)
public class SignedMessageMixin implements ExtSignedMessage {
    @Shadow
    @Final
    private MessageBody signedBody;
    @Unique
    private final Map<String, Text> styledChat_args = new HashMap<>();
    @Unique
    private RegistryKey<MessageType> styledChat_type = null;

    @Unique
    private ServerCommandSource styledChat_source = null;

    @Override
    public void styledChat_setArg(String name, Text arg) {
        this.styledChat_args.put(name, arg);
    }

    @Override
    public String styledChat_getOriginal() {
        return this.signedBody.content();
    }

    @Override
    public Text styledChat_getArg(String name) {
        return this.styledChat_args.getOrDefault(name, StyledChatUtils.EMPTY_TEXT);
    }

    @Override
    public void styledChat_setType(RegistryKey<MessageType> type) {
        this.styledChat_type = type;
    }

    @Override
    public RegistryKey<MessageType> styledChat_getType() {
        return this.styledChat_type;
    }

    @Override
    public void styledChat_setSource(ServerCommandSource source) {
        this.styledChat_source = source;
    }

    @Override
    public @Nullable ServerCommandSource styledChat_getSource() {
        return this.styledChat_source;
    }

    @Inject(method = "withUnsignedContent", at = @At("RETURN"))
    private void styledChat$copyData1(Text unsignedContent, CallbackInfoReturnable<SignedMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Inject(method = "withFilterMask", at = @At("RETURN"))
    private void styledChat$copyData2(FilterMask filterMask, CallbackInfoReturnable<SignedMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Inject(method = "withoutUnsigned", at = @At("RETURN"))
    private void styledChat$copyData3(CallbackInfoReturnable<SignedMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Inject(method = "withFilterMaskEnabled", at = @At("RETURN"))
    private void styledChat$copyData4(boolean enabled, CallbackInfoReturnable<SignedMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Unique
    private void styledChat$copyData(SignedMessage returnValue) {
        var mixin = (SignedMessageMixin) (Object) returnValue;

        if ((Object) returnValue == this) {
            return;
        }

        mixin.styledChat_type = this.styledChat_type;
        mixin.styledChat_args.putAll(this.styledChat_args);
        mixin.styledChat_source = this.styledChat_source;
    }
}
