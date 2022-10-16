package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow
    @Final
    private MinecraftServer server;
    @Unique
    private ServerPlayerEntity styledChat_temporaryPlayer = null;

    @Inject(method = "onPlayerConnect", at = @At(value = "HEAD"))
    private void styledChat_storePlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.styledChat_temporaryPlayer = player;
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void styledChat_removeStoredPlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.styledChat_temporaryPlayer = null;
    }

    @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private Text styledChat_updatePlayerNameAfterMessage(Text text) {
        if (this.styledChat_temporaryPlayer.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
            return StyledChatStyles.getJoinFirstTime(this.styledChat_temporaryPlayer);
        }

        Object[] args = ((TranslatableTextContent) text.getContent()).getArgs();
        if (args.length == 1) {
            return StyledChatStyles.getJoin(this.styledChat_temporaryPlayer);
        } else {
            return StyledChatStyles.getJoinRenamed(this.styledChat_temporaryPlayer, (String) args[1]);
        }
    }

    @Inject(method = "sendCommandTree(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("HEAD"))
    private void styledChat_sendTree(ServerPlayerEntity player, CallbackInfo ci) {
        StyledChatUtils.sendAutocompliton(player);
    }

    @Redirect(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageSourceProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;logChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageType$Parameters;Ljava/lang/String;)V"), require = 0)
    private void styledChat_fixServerLogs(MinecraftServer instance, Text text, MessageType.Parameters parameters, String string, SignedMessage signedMessage) {
        var out = ((ExtSignedMessage) (Object) signedMessage).styledChat_getArg("override");
        if (out != null) {
            if (out != StyledChatUtils.IGNORED_TEXT) {
                this.server.sendMessage(out);
            }
        } else {
            this.server.logChatMessage(text, parameters, string);
        }
    }
}
