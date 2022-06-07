package eu.pb4.styledchat.mixin;

import com.mojang.brigadier.context.CommandContextBuilder;
import eu.pb4.styledchat.ducks.ExtMessageFormat;
import net.minecraft.command.argument.DecoratableArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(DecoratableArgumentType.class)
public interface DecoratableArgumentTypeMixin {
    @Redirect(method = "decorate(Lcom/mojang/brigadier/context/CommandContextBuilder;Ljava/lang/String;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/context/CommandContextBuilder;getArguments()Ljava/util/Map;"))
    private Map styledChat_setContext(CommandContextBuilder<ServerCommandSource> instance) {
        var map = instance.getArguments();
        for (var e : map.values()) {
            if (e.getResult() instanceof ExtMessageFormat a) {
                a.styledChat_setSource(instance.getNodes().get(0).getNode().getName(), instance.getSource(), (i, x) -> instance.getArguments().get(i).getResult());
            }
        }
        return map;
    }
}
