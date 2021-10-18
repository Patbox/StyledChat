package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;


@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @ModifyArg(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private Text styledChat_replaceDeathMessage(Text text) {
        return ConfigManager.getConfig().getDeath((ServerPlayerEntity) (Object) this, text);
    }

    @Inject(method = "sendMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(Text message, MessageType type, UUID sender, CallbackInfo ci) {
        if (message instanceof TranslatableText text && text.getKey().equals(StyledChatUtils.IGNORED_TEXT_KEY)) {
            ci.cancel();
        }
    }
}
