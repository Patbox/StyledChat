package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementsMixin {

    @Shadow private ServerPlayer player;

    @ModifyArg(method = "method_53637", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private Component styledChat_changeAdvancementMessage(Component text) {
        var translatableText = (TranslatableContents) text.getContents();
        Component advancement = (Component) translatableText.getArgs()[1];

        return switch (translatableText.getKey()) {
            case "chat.type.advancement.task" -> StyledChatStyles.getAdvancementTask(this.player, advancement);
            case "chat.type.advancement.goal" -> StyledChatStyles.getAdvancementGoal(this.player, advancement);
            case "chat.type.advancement.challenge" -> StyledChatStyles.getAdvancementChallenge(this.player, advancement);
            default -> text;
        };
    }
}
