package eu.pb4.styledchat;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public final class StyledChatStyles {
    public static Text getDisplayName(ServerPlayerEntity player, Text vanillaDisplayName) {
        var style = StyledChatUtils.getPersonalStyle(player).getDisplayName(player, vanillaDisplayName);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getDisplayName(player, vanillaDisplayName);
    }

    public static Text getChat(ServerPlayerEntity player, Text message) {
        var style = StyledChatUtils.getPersonalStyle(player).getChat(player, message);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getChat(player, message);
    }

    public static Text getJoin(ServerPlayerEntity player) {
        var style = StyledChatUtils.getPersonalStyle(player).getJoin(player);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getJoin(player);
    }

    public static Text getJoinFirstTime(ServerPlayerEntity player) {
        var style = StyledChatUtils.getPersonalStyle(player).getJoinFirstTime(player);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getJoinFirstTime(player);
    }

    public static Text getJoinRenamed(ServerPlayerEntity player, String oldName) {
        var style = StyledChatUtils.getPersonalStyle(player).getJoinRenamed(player, oldName);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getJoinRenamed(player, oldName);
    }

    public static Text getLeft(ServerPlayerEntity player) {
        var style = StyledChatUtils.getPersonalStyle(player).getLeft(player);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getLeft(player);
    }

    public static Text getDeath(ServerPlayerEntity player, Text vanillaMessage) {
        var style = StyledChatUtils.getPersonalStyle(player).getDeath(player, vanillaMessage);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getDeath(player, vanillaMessage);
    }

    public static Text getAdvancementTask(ServerPlayerEntity player, Text advancement) {
        var style = StyledChatUtils.getPersonalStyle(player).getAdvancementTask(player, advancement);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getAdvancementTask(player, advancement);
    }

    public static Text getAdvancementGoal(ServerPlayerEntity player, Text advancement) {
        var style = StyledChatUtils.getPersonalStyle(player).getAdvancementGoal(player, advancement);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getAdvancementGoal(player, advancement);
    }

    public static Text getAdvancementChallenge(ServerPlayerEntity player, Text advancement) {
        var style = StyledChatUtils.getPersonalStyle(player).getAdvancementChallenge(player, advancement);
        if (style != null) {
            return style;
        }

        return ConfigManager.getConfig().getAdvancementChallenge(player, advancement);
    }

    public static Text getSayCommand(ServerCommandSource source, Text message) {
        if (source.isExecutedByPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getSayCommand(source, message);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getSayCommand(source, message);
    }

    public static Text getMeCommand(ServerCommandSource source, Text message) {
        if (source.isExecutedByPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getMeCommand(source, message);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getMeCommand(source, message);
    }

    public static Text getPrivateMessageSent(Text sender, Text receiver, Text message, ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getPrivateMessageSent(sender, receiver, message, PlaceholderContext.of(source));
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getPrivateMessageSent(sender, receiver, message, source);
    }

    public static Text getPrivateMessageReceived(Text sender, Text receiver, Text message, ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getPrivateMessageReceived(sender, receiver, message, PlaceholderContext.of(source));
            if (style != null) {
                return style;
            }
        }
        return ConfigManager.getConfig().getPrivateMessageReceived(sender, receiver, message, source);
    }

    public static Text getTeamChatSent(Text team, Text displayName, Text message, ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getTeamChatSent(team, displayName, message, source);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getTeamChatSent(team, displayName, message, source);
    }

    public static Text getTeamChatReceived(Text team, Text displayName, Text message, ServerCommandSource source) {
        if (source.isExecutedByPlayer()) {
            var style = StyledChatUtils.getPersonalStyle(source.getPlayer()).getTeamChatReceived(team, displayName, message, source);
            if (style != null) {
                return style;
            }
        }

        return ConfigManager.getConfig().getTeamChatReceived(team, displayName, message, source);
    }

    public static Text getPetDeath(TameableEntity entity, Text vanillaMessage) {
        return ConfigManager.getConfig().getPetDeath(entity, vanillaMessage);
    }

    public static Map<String, TextNode> getEmotes(ServerCommandSource source) {
        return ConfigManager.getConfig().getEmotes(source);

    }
}
