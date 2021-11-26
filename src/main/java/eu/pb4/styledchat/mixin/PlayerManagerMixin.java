package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Function;


@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Unique private ServerPlayerEntity temporaryPlayer = null;

    @Inject(method = "onPlayerConnect", at = @At(value = "HEAD"))
    private void styledChat_storePlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.temporaryPlayer = player;
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void styledChat_removeStoredPlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.temporaryPlayer = null;
    }

    @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private Text styledChat_updatePlayerNameAfterMessage(Text text) {
        if (this.temporaryPlayer.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
            return ConfigManager.getConfig().getJoinFirstTime(this.temporaryPlayer);
        }

        Object[] args = ((TranslatableText) text).getArgs();
        if (args.length == 1) {
            return ConfigManager.getConfig().getJoin(this.temporaryPlayer);
        } else {
            return ConfigManager.getConfig().getJoinRenamed(this.temporaryPlayer, (String) args[1]);
        }
    }

    @Inject(method = "broadcast", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(Text message, MessageType type, UUID sender, CallbackInfo ci) {
        if (message instanceof TranslatableText text && text.getKey().equals(StyledChatUtils.IGNORED_TEXT_KEY)) {
            ci.cancel();
        }
    }
}
