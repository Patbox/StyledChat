package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(value = Player.class, priority = 700)
public abstract class PlayerMixin {
    @Unique
    private Component styledChat$cachedName = Component.empty();

    @Unique
    private int styledChat$cachedAge = -1234568;

    @Unique
    private boolean styledChat$ignoreNextCalls = false;

    @Unique
    private Component styledChat$previousInput;

    @Inject(method = "getDisplayName", at = @At("TAIL"), cancellable = true)
    private void styledChat_replaceDisplayName(CallbackInfoReturnable<Component> cir) {
        if (!this.styledChat$ignoreNextCalls && ((Object) this).getClass() == ServerPlayer.class) {
            var input = cir.getReturnValue();
            var player = (ServerPlayer) (Object) this;

            if (this.styledChat$cachedAge == ((Entity) (Object) this).tickCount
                    && (this.styledChat$previousInput == null || Objects.equals(this.styledChat$previousInput, input))) {
                cir.setReturnValue(this.styledChat$cachedName);
                return;
            }

            boolean isMainThread = player.level().getServer().isSameThread();

            // If not on the main thread, return the cached value without updating state
            if (!isMainThread) {
                if (this.styledChat$cachedName != null) {
                    cir.setReturnValue(this.styledChat$cachedName);
                }
                return;
            }


            this.styledChat$previousInput = input;
            this.styledChat$ignoreNextCalls = true;
            var name = StyledChatStyles.getDisplayName(player, input);
            this.styledChat$ignoreNextCalls = false;
            this.styledChat$cachedName = name;
            this.styledChat$cachedAge = ((Entity) (Object) this).tickCount;
            cir.setReturnValue(name);
        }
    }
}
