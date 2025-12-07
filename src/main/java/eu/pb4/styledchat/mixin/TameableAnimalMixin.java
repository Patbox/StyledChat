package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TamableAnimal.class)
public class TameableAnimalMixin {
    @ModifyArg(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;sendSystemMessage(Lnet/minecraft/network/chat/Component;)V"))
    private Component styledChat_replaceDeathMessage(Component text) {
        return StyledChatStyles.getPetDeath((TamableAnimal) (Object) this, text);
    }
}
