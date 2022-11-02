package eu.pb4.styledchat.mixin;

import com.mojang.datafixers.util.Pair;
import eu.pb4.styledchat.StyledChatMod;
import net.minecraft.network.message.MessageType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Decoration;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    @Inject(method = "load(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/util/registry/DynamicRegistryManager$Immutable;", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void styledChat$injectMessageTypes(ResourceManager resourceManager, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries,
                                                      CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir, Map _unused, List<Pair<MutableRegistry<?>, Object>> list) {

        for (var pair : list) {
            var reg = pair.getFirst();
            if (reg.getKey().equals(Registry.MESSAGE_TYPE_KEY)) {
                Registry.register((Registry<MessageType>) reg, StyledChatMod.MESSAGE_TYPE_ID, new MessageType(Decoration.ofChat("%s"), Decoration.ofChat("%s")));
            }
        }
    }
}
