package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(Text message, CallbackInfo ci) {
        if (message == StyledChatUtils.IGNORED_TEXT) {
            ci.cancel();
        }
    }
}
