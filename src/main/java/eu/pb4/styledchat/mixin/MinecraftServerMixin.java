package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "sendSystemMessage", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(Text message, UUID sender, CallbackInfo ci) {
        if (message instanceof TranslatableText text && text.getKey().equals(StyledChatUtils.IGNORED_TEXT_KEY)) {
            ci.cancel();
        }
    }
}
