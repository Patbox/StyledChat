package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatMod;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.*;
import net.minecraft.text.Decoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    @Inject(method = "load(Lnet/minecraft/registry/RegistryLoader$RegistryLoadable;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void styledChat$injectMessageTypes(@Coerce Object registryLoadable, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries, CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir, Map map,
                                                      List<RegistryLoader.class_9158<?>> list, RegistryOps.RegistryInfoGetter registryInfoGetter) {

        for (var entry : list) {
            var reg = entry.registry();
            if (reg.getKey().equals(RegistryKeys.MESSAGE_TYPE)) {
                Registry.register((Registry<MessageType>) reg, StyledChatMod.MESSAGE_TYPE_ID, new MessageType(Decoration.ofChat("%s"), Decoration.ofChat("%s")));
            }
        }
    }
}
