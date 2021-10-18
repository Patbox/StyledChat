package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {
    @ModifyArg(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;sendSystemMessage(Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private Text styledChat_replaceDeathMessage(Text text) {
        return ConfigManager.getConfig().getPetDeath((TameableEntity) (Object) this, text);
    }
}
