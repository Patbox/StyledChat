package eu.pb4.styledchat.mixin.commands;

import com.mojang.brigadier.context.CommandContext;
import eu.pb4.styledchat.StyledChatUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.EmoteCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmoteCommands.class)
public class EmoteCommandMixin {
    @Inject(method = "method_43645", at = @At("HEAD"))
    private static void styledChat_formatText(CommandContext<CommandSourceStack> commandContext, PlayerChatMessage signedMessage, CallbackInfo ci) {
        StyledChatUtils.modifyForSending(signedMessage, commandContext.getSource(), ChatType.EMOTE_COMMAND);
    }
}
