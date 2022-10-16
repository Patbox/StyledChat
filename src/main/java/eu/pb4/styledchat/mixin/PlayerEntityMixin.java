package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private boolean ignoreNextCalls = false;

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void styledChat_replaceDisplayName(CallbackInfoReturnable<Text> cir) {
        if (!this.ignoreNextCalls && ((Object) this) instanceof ServerPlayerEntity player) {
            this.ignoreNextCalls = true;
            cir.setReturnValue(StyledChatStyles.getDisplayName(player, cir.getReturnValue()));
            this.ignoreNextCalls = false;
        }
    }
}
