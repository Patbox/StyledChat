package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.config.Config;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow private ServerPlayerEntity owner;

    @ModifyArg(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private Text changeAdvancementMessage(Text text) {
        TranslatableText translatableText = (TranslatableText) text;

        Config config = ConfigManager.getConfig();
        Text advancement = (Text) translatableText.getArgs()[1];

        return switch (translatableText.getKey()) {
            case "chat.type.advancement.task" -> config.getAdvancementTask(this.owner, advancement);
            case "chat.type.advancement.goal" -> config.getAdvancementGoal(this.owner, advancement);
            case "chat.type.advancement.challenge" -> config.getAdvancementChallenge(this.owner, advancement);
            default -> text;
        };
    }
}