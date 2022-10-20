package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatMod;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Decoration;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MessageType.class)
public class MessageTypeMixin {
    @Inject(method = "initialize", at = @At("TAIL"))
    private static void styledChat_replace(Registry<MessageType> registry, CallbackInfoReturnable<RegistryEntry<MessageType>> cir) {
        BuiltinRegistries.add(registry, StyledChatMod.MESSAGE_TYPE_ID,  new MessageType(Decoration.ofChat("%s"), Decoration.ofChat("%s")));
    }
}
