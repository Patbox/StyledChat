package eu.pb4.styledchat.mixin.commands;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MeCommand.class)
public class MeCommandMixin {
    @Inject(method = "method_43645", at = @At("HEAD"))
    private static void styledChat_formatText(CommandContext<ServerCommandSource> commandContext, SignedMessage signedMessage, CallbackInfo ci) {
        StyledChatUtils.modifyForSending(signedMessage, commandContext.getSource(), MessageType.EMOTE_COMMAND);
    }
}
