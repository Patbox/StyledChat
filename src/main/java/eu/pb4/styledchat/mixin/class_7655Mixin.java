package eu.pb4.styledchat.mixin;

import com.mojang.datafixers.util.Pair;
import eu.pb4.styledchat.StyledChatMod;
import net.minecraft.class_7655;
import net.minecraft.network.message.MessageType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Decoration;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(class_7655.class)
public class class_7655Mixin {
    @Inject(method = "method_45121", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void styledChat$injectMessageTypes(ResourceManager resourceManager, DynamicRegistryManager dynamicRegistryManager,
                                                      List<class_7655.class_7657<?>> list, CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir,
                                                      Map<RegistryKey<?>, Exception> _unused, List<Pair<Registry<?>, Object>> _unused2,
                                                      DynamicRegistryManager dynamicRegistryManager2
                                                      ) {
        var reg = dynamicRegistryManager2.getOptional(Registry.MESSAGE_TYPE_KEY);
        if (reg.isPresent()) {
            Registry.register(reg.get(), StyledChatMod.MESSAGE_TYPE_ID, new MessageType(Decoration.ofChat("%s"), Decoration.ofChat("%s")));
        }
    }
}
