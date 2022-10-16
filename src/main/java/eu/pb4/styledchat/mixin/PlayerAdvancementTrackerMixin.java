package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @ModifyArg(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private Text styledChat_changeAdvancementMessage(Text text) {
        var translatableText = (TranslatableTextContent) text.getContent();
        Text advancement = (Text) translatableText.getArgs()[1];

        return switch (translatableText.getKey()) {
            case "chat.type.advancement.task" -> StyledChatStyles.getAdvancementTask(this.owner, advancement);
            case "chat.type.advancement.goal" -> StyledChatStyles.getAdvancementGoal(this.owner, advancement);
            case "chat.type.advancement.challenge" -> StyledChatStyles.getAdvancementChallenge(this.owner, advancement);
            default -> text;
        };
    }
}
