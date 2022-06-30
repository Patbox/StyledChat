package eu.pb4.styledchat.mixin;

import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Function;


@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow @Final private MinecraftServer server;
    @Unique private ServerPlayerEntity temporaryPlayer = null;

    @Inject(method = "onPlayerConnect", at = @At(value = "HEAD"))
    private void styledChat_storePlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.temporaryPlayer = player;
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void styledChat_removeStoredPlayer(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        this.temporaryPlayer = null;
    }

    @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V"))
    private Text styledChat_updatePlayerNameAfterMessage(Text text) {
        if (this.temporaryPlayer.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) == 0) {
            return ConfigManager.getConfig().getJoinFirstTime(this.temporaryPlayer);
        }

        Object[] args = ((TranslatableTextContent) text.getContent()).getArgs();
        if (args.length == 1) {
            return ConfigManager.getConfig().getJoin(this.temporaryPlayer);
        } else {
            return ConfigManager.getConfig().getJoinRenamed(this.temporaryPlayer, (String) args[1]);
        }
    }

    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages(Text message, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
        if (message == StyledChatUtils.IGNORED_TEXT) {
            ci.cancel();
        }
    }

    @Inject(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
    private void styledChat_excludeSendingOfHiddenMessages2(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
        if (message == StyledChatUtils.IGNORED_TEXT) {
            ci.cancel();
        }
    }

    @Redirect(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Function;Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;logChatMessage(Lnet/minecraft/network/message/MessageSender;Lnet/minecraft/text/Text;)V"), require = 0)
    private void styledChat_fixServerLogs(MinecraftServer instance, MessageSender sender, Text ignore, SignedMessage message) {
        var out = ((ExtSignedMessage) (Object) message).styledChat_getArg("override");
        if (out != null) {
            if (out != StyledChatUtils.IGNORED_TEXT) {
                this.server.sendMessage(out);
            }
        } else {
            this.server.logChatMessage(sender, message.getContent());
        }
    }
}
