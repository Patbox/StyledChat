package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.ducks.ExtMessageFormat;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.BiFunction;

@Mixin(MessageArgument.Message.class)
public class MessageMixin implements ExtMessageFormat {
    @Unique
    private String styledChat_context;
    @Unique
    private CommandSourceStack styledChat_source;
    private BiFunction<String, Class<?>, Object> styledChat_args;

    /*@Redirect(method = "method_45566", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getMessageDecorator()Lnet/minecraft/network/message/MessageDecorator;"))
    private <T> MessageDecorator styledChat_replaceDecorator2(MinecraftServer instance) {
        if (this.styledChat_context != null && ConfigManager.getConfig().configData.chatPreview.sendFullMessage) {
            return StyledChatUtils.getCommandDecorator(this.styledChat_context, this.styledChat_source, this.styledChat_args);
        }
        return StyledChatUtils.getRawDecorator();
    }*/

    @Override
    public <T> void styledChat_setSource(String command, CommandSourceStack source, BiFunction<String, Class<T>, T> argumentGetter) {
        this.styledChat_context = command;
        this.styledChat_source = source;
        this.styledChat_args = (BiFunction<String, Class<?>, Object>) (Object) argumentGetter;
    }
}