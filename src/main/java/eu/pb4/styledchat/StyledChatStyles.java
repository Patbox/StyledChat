package eu.pb4.styledchat;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.styledchat.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;

public final class StyledChatStyles {
    public static Component getDisplayName(ServerPlayer player, Component vanillaDisplayName) {
        var style = StyledChatUtils.getPersonalStyle(player).getDisplayName(player, vanillaDisplayName);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getDisplayName(player, vanillaDisplayName);
    }

    public static Component getChat(ServerPlayer player, Component message) {
        var style = StyledChatUtils.getPersonalStyle(player).getChat(player, message);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getChat(player, message);
    }

    public static Component getJoin(ServerPlayer player) {
        var style = StyledChatUtils.getPersonalStyle(player).getJoin(player);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getJoin(player);
    }

    public static Component getJoinFirstTime(ServerPlayer player) {
        var style = StyledChatUtils.getPersonalStyle(player).getJoinFirstTime(player);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getJoinFirstTime(player);
    }

    public static Component getJoinRenamed(ServerPlayer player, String oldName) {
        var style = StyledChatUtils.getPersonalStyle(player).getJoinRenamed(player, oldName);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getJoinRenamed(player, oldName);
    }

    public static Component getLeft(ServerPlayer player) {
        var style = StyledChatUtils.getPersonalStyle(player).getLeft(player);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getLeft(player);
    }

    public static Component getDeath(ServerPlayer player, Component vanillaMessage) {
        var style = StyledChatUtils.getPersonalStyle(player).getDeath(player, vanillaMessage);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getDeath(player, vanillaMessage);
    }

    public static Component getAdvancementTask(ServerPlayer player, Component advancement) {
        var style = StyledChatUtils.getPersonalStyle(player).getAdvancementTask(player, advancement);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getAdvancementTask(player, advancement);
    }

    public static Component getAdvancementGoal(ServerPlayer player, Component advancement) {
        var style = StyledChatUtils.getPersonalStyle(player).getAdvancementGoal(player, advancement);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getAdvancementGoal(player, advancement);
    }

    public static Component getAdvancementChallenge(ServerPlayer player, Component advancement) {
        var style = StyledChatUtils.getPersonalStyle(player).getAdvancementChallenge(player, advancement);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getAdvancementChallenge(player, advancement);
    }

    public static Component getSayCommand(CommandSourceStack source, Component message) {
        if (source.isPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getSayCommand(source, message);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getSayCommand(source, message);
    }

    public static Component getMeCommand(CommandSourceStack source, Component message) {
        if (source.isPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getMeCommand(source, message);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getMeCommand(source, message);
    }

    public static Component getPrivateMessageSent(Component sender, Component receiver, Component message, CommandSourceStack source) {
        if (source.isPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getPrivateMessageSent(sender, receiver, message, PlaceholderContext.of(source));
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getPrivateMessageSent(sender, receiver, message, source);
    }

    public static Component getPrivateMessageReceived(Component sender, Component receiver, Component message, CommandSourceStack source) {
        if (source.isPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getPrivateMessageReceived(sender, receiver, message, PlaceholderContext.of(source));
            if (style != null) {
                return style;
            }
        }
        return ConfigManager.getConfig().getPrivateMessageReceived(sender, receiver, message, source);
    }

    public static Component getTeamChatSent(Component team, Component displayName, Component message, CommandSourceStack source) {
        if (source.isPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getTeamChatSent(team, displayName, message, source);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getTeamChatSent(team, displayName, message, source);
    }

    public static Component getTeamChatReceived(Component team, Component displayName, Component message, CommandSourceStack source) {
        if (source.isPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getTeamChatReceived(team, displayName, message, source);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getTeamChatReceived(team, displayName, message, source);
    }

    public static Component getPetDeath(TamableAnimal entity, Component vanillaMessage) {
        return ConfigManager.getConfig().getPetDeath(entity, vanillaMessage);
    }

    public static Map<String, TextNode> getEmotes(CommandSourceStack source) {
        return ConfigManager.getConfig().getEmotes(source);

    }

    public static Component getCustom(Identifier identifier, Component displayName, Component message, Component receiver, CommandSourceStack source) {
        if (source.isPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getCustom(identifier, displayName, message, receiver, source);
            if (style != null) {
                return style;
            }
        }

        var out = ConfigManager.getConfig().getCustom(identifier, displayName, message, receiver, source);

        if (out != null) {
            return out;
        }

        var type = source.registryAccess().lookupOrThrow(Registries.CHAT_TYPE).getValue(identifier);

        if (type == null) {
            return Component.empty();
        }

        var optional = source.registryAccess().lookupOrThrow(Registries.CHAT_TYPE).getResourceKey(type);

        if (optional.isEmpty()) {
            return Component.empty();
        }
        var params = ChatType.bind(optional.get(), source.registryAccess(), displayName);

        if (receiver != null) {
            params = params.withTargetName(receiver);
        }


        return type.chat().decorate(message, params);
    }
}
