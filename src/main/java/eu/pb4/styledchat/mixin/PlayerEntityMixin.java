package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 700)
public abstract class PlayerEntityMixin {
    @Unique
    private Text styledChat$cachedName = Text.empty();

    @Unique
    private int styledChat$cachedAge = -1234568;

    @Unique
    private boolean styledChat$ignoreNextCalls = false;

    @Inject(method = "getDisplayName", at = @At("TAIL"), cancellable = true)
    private void styledChat_replaceDisplayName(CallbackInfoReturnable<Text> cir) {
        if (!this.styledChat$ignoreNextCalls && ((Object) this).getClass() == ServerPlayerEntity.class) {
            if (this.styledChat$cachedAge == ((Entity) (Object) this).age) {
                cir.setReturnValue(this.styledChat$cachedName);
                return;
            }

            this.styledChat$ignoreNextCalls = true;
            var name = StyledChatStyles.getDisplayName((ServerPlayerEntity) (Object) this, cir.getReturnValue());
            this.styledChat$ignoreNextCalls = false;
            this.styledChat$cachedName = name;
            this.styledChat$cachedAge = ((Entity) (Object) this).age;
            cir.setReturnValue(name);
        }
    }
}
