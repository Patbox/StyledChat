package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtPlayerChatMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(PlayerChatMessage.class)
public class PlayerChatMessageMixin implements ExtPlayerChatMessage {
    @Shadow
    @Final
    private SignedMessageBody signedBody;
    @Unique
    private final Map<String, Component> styledChat_args = new HashMap<>();
    @Unique
    private ResourceKey<ChatType> styledChat_type = null;

    @Unique
    private CommandSourceStack styledChat_source = null;

    @Override
    public void styledChat_setArg(String name, Component arg) {
        this.styledChat_args.put(name, arg);
    }

    @Override
    public String styledChat_getOriginal() {
        return this.signedBody.content();
    }

    @Override
    public Component styledChat_getArg(String name) {
        return this.styledChat_args.getOrDefault(name, StyledChatUtils.EMPTY_TEXT);
    }

    @Override
    public void styledChat_setType(ResourceKey<ChatType> type) {
        this.styledChat_type = type;
    }

    @Override
    public ResourceKey<ChatType> styledChat_getType() {
        return this.styledChat_type;
    }

    @Override
    public void styledChat_setSource(CommandSourceStack source) {
        this.styledChat_source = source;
    }

    @Override
    public @Nullable CommandSourceStack styledChat_getSource() {
        return this.styledChat_source;
    }

    @Inject(method = "withUnsignedContent", at = @At("RETURN"))
    private void styledChat$copyData1(Component unsignedContent, CallbackInfoReturnable<PlayerChatMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Inject(method = "filter(Lnet/minecraft/network/chat/FilterMask;)Lnet/minecraft/network/chat/PlayerChatMessage;", at = @At("RETURN"))
    private void styledChat$copyData2(FilterMask filterMask, CallbackInfoReturnable<PlayerChatMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Inject(method = "removeUnsignedContent", at = @At("RETURN"))
    private void styledChat$copyData3(CallbackInfoReturnable<PlayerChatMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Inject(method = "filter(Z)Lnet/minecraft/network/chat/PlayerChatMessage;", at = @At("RETURN"))
    private void styledChat$copyData4(boolean enabled, CallbackInfoReturnable<PlayerChatMessage> cir) {
        this.styledChat$copyData(cir.getReturnValue());
    }

    @Unique
    private void styledChat$copyData(PlayerChatMessage returnValue) {
        var mixin = (PlayerChatMessageMixin) (Object) returnValue;

        if ((Object) returnValue == this) {
            return;
        }

        mixin.styledChat_type = this.styledChat_type;
        mixin.styledChat_args.putAll(this.styledChat_args);
        mixin.styledChat_source = this.styledChat_source;
    }
}
