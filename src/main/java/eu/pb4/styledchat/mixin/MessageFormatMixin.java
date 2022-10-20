package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.ducks.ExtMessageFormat;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiFunction;

@Mixin(MessageArgumentType.MessageFormat.class)
public class MessageFormatMixin implements ExtMessageFormat {
    @Unique
    private String styledChat_context;
    @Unique
    private ServerCommandSource styledChat_source;
    private BiFunction<String, Class<?>, Object> styledChat_args;

    /*@Redirect(method = "method_45566", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getMessageDecorator()Lnet/minecraft/network/message/MessageDecorator;"))
    private <T> MessageDecorator styledChat_replaceDecorator2(MinecraftServer instance) {
        if (this.styledChat_context != null && ConfigManager.getConfig().configData.chatPreview.sendFullMessage) {
            return StyledChatUtils.getCommandDecorator(this.styledChat_context, this.styledChat_source, this.styledChat_args);
        }
        return StyledChatUtils.getRawDecorator();
    }*/

    @Override
    public <T> void styledChat_setSource(String command, ServerCommandSource source, BiFunction<String, Class<T>, T> argumentGetter) {
        this.styledChat_context = command;
        this.styledChat_source = source;
        this.styledChat_args = (BiFunction<String, Class<?>, Object>) (Object) argumentGetter;
    }
}