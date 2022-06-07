package eu.pb4.styledchat.mixin;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.styledchat.ducks.ExtMessageFormat;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(MessageArgumentType.class)
public class MessageArgumentTypeMixin {

    @Redirect(method = { "getMessage", "getSignedMessage" }, at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/context/CommandContext;getArgument(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"))
    private static Object styledChat_setContext(CommandContext instance, String name, Class<Object> clazz) {
        var obj = (MessageArgumentType.MessageFormat) instance.getArgument(name, MessageArgumentType.MessageFormat.class);
        ((ExtMessageFormat) obj).styledChat_setSource(instance.getInput(), (ServerCommandSource) instance.getSource(), instance::getArgument);
        return obj;
    }
}
