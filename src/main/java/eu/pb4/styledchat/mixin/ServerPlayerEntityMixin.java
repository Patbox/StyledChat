package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @ModifyArg(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private Text replaceDeathMessage(Text text) {
        return ConfigManager.getConfig().getDeath((ServerPlayerEntity) (Object) this, text);
    }
}
