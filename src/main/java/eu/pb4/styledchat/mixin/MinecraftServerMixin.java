package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V"))
    private void styledChat_registerStarting(CallbackInfo ci) {
        StyledChatMod.serverStarting((MinecraftServer) (Object) this);
    }

    @Inject(method = "shutdown", at = @At("TAIL"))
    private void styledChat_registerStopping(CallbackInfo ci) {
        StyledChatMod.serverStopped((MinecraftServer) (Object) this);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(Text message, CallbackInfo ci) {
        if (message == StyledChatUtils.IGNORED_TEXT) {
            ci.cancel();
        }
    }
}
