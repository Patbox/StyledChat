package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatStyles;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.ducks.ExtPlayerChatMessage;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Shadow
    @Final
    private MinecraftServer server;
    @Unique
    private ServerPlayer styledChat_temporaryPlayer = null;

    @Inject(method = "placeNewPlayer", at = @At(value = "HEAD"))
    private void styledChat_storePlayer(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        this.styledChat_temporaryPlayer = player;
    }

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    private void styledChat_removeStoredPlayer(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        this.styledChat_temporaryPlayer = null;
    }

    @ModifyArg(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private Component styledChat_updatePlayerNameAfterMessage(Component text) {
        if (this.styledChat_temporaryPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0) {
            return StyledChatStyles.getJoinFirstTime(this.styledChat_temporaryPlayer);
        }

        Object[] args = ((TranslatableContents) text.getContents()).getArgs();
        if (args.length == 1) {
            return StyledChatStyles.getJoin(this.styledChat_temporaryPlayer);
        } else {
            return StyledChatStyles.getJoinRenamed(this.styledChat_temporaryPlayer, (String) args[1]);
        }
    }

    @Inject(method = "sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("HEAD"))
    private void styledChat_sendTree(ServerPlayer player, CallbackInfo ci) {
        StyledChatUtils.sendAutoCompletion(player, ConfigManager.getConfig().allPossibleAutoCompletionKeys);
    }

    @Redirect(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;logChatMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType$Bound;Ljava/lang/String;)V"), require = 0)
    private void styledChat_fixServerLogs(MinecraftServer instance, Component text, ChatType.Bound parameters, String string, PlayerChatMessage signedMessage) {
        var out = ((ExtPlayerChatMessage) (Object) signedMessage).styledChat_getArg("override");
        if (out != null) {
            if (out != StyledChatUtils.IGNORED_TEXT) {
                this.server.sendSystemMessage(out);
            }
        } else {
            this.server.logChatMessage(text, parameters, string);
        }
    }
}
