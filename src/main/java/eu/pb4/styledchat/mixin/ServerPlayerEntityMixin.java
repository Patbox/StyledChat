package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"))
    private Text styledChat$replaceDeathMessage(DamageTracker instance) {
        return StyledChatStyles.getDeath((ServerPlayerEntity) (Object) this, instance.getDeathMessage());
    }

    @Inject(method = "sendMessageToClient", at = @At("HEAD"), cancellable = true)
    private void styledChat$excludeSendingOfHiddenMessages(Text message, boolean ignore, CallbackInfo ci) {
        if (message == StyledChatUtils.IGNORED_TEXT) {
            ci.cancel();
        }
    }
}
