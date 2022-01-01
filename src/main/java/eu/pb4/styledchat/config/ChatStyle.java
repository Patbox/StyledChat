package eu.pb4.styledchat.config;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.data.ChatStyleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Map;

public class ChatStyle {
    public final Text displayName;
    public final Text chat;
    public final Text join;
    public final Text joinFirstTime;
    public final Text joinRenamed;
    public final Text left;
    public final Text death;
    public final Text advancementTask;
    public final Text advancementChallenge;
    public final Text advancementGoal;
    public final Text privateMessageSent;
    public final Text privateMessageReceived;
    public final Text teamChatSent;
    public final Text teamChatReceived;
    public final Text sayCommand;
    public final Text meCommand;

    public ChatStyle(ChatStyleData data, ChatStyle defaultStyle) {
        this.displayName = data.displayName != null ? StyledChatUtils.parseText(data.displayName.replace("%player:displayname%", "")) : defaultStyle.displayName;
        this.chat = data.chat != null ? StyledChatUtils.parseText(data.chat) : defaultStyle.chat;
        this.join = data.join != null ? StyledChatUtils.parseText(data.join) : defaultStyle.join;
        this.joinFirstTime = data.joinFirstTime != null ? StyledChatUtils.parseText(data.joinFirstTime) : this.join;
        this.joinRenamed = data.joinRenamed != null ? StyledChatUtils.parseText(data.joinRenamed) : defaultStyle.joinRenamed;
        this.left = data.left != null ? StyledChatUtils.parseText(data.left) : defaultStyle.left;
        this.death = data.death != null ? StyledChatUtils.parseText(data.death) : defaultStyle.death;
        this.advancementTask = data.advancementTask != null ? StyledChatUtils.parseText(data.advancementTask) : defaultStyle.advancementTask;
        this.advancementChallenge = data.advancementChallenge != null ? StyledChatUtils.parseText(data.advancementChallenge) : defaultStyle.advancementChallenge;
        this.advancementGoal = data.advancementGoal != null ? StyledChatUtils.parseText(data.advancementGoal) : defaultStyle.advancementGoal;
        this.privateMessageSent = data.privateMessageSent != null ? StyledChatUtils.parseText(data.privateMessageSent) : defaultStyle.privateMessageSent;
        this.privateMessageReceived = data.privateMessageReceived != null ? StyledChatUtils.parseText(data.privateMessageReceived) : defaultStyle.privateMessageReceived;
        this.teamChatSent = data.teamChatSent != null ? StyledChatUtils.parseText(data.teamChatSent) : defaultStyle.teamChatSent;
        this.teamChatReceived = data.teamChatReceived != null ? StyledChatUtils.parseText(data.teamChatReceived) : defaultStyle.teamChatReceived;
        this.sayCommand = data.sayCommand != null ? StyledChatUtils.parseText(data.sayCommand) : defaultStyle.sayCommand;
        this.meCommand = data.meCommand != null ? StyledChatUtils.parseText(data.meCommand) : defaultStyle.meCommand;

    }

    public ChatStyle(ChatStyleData data) {
        this.displayName = data.displayName != null ? StyledChatUtils.parseText(data.displayName.replace("%player:displayname%", "")) : null;
        this.chat = data.chat != null ? StyledChatUtils.parseText(data.chat) : null;
        this.join = data.join != null ? StyledChatUtils.parseText(data.join) : null;
        this.joinRenamed = data.joinRenamed != null ? StyledChatUtils.parseText(data.joinRenamed) : null;
        this.joinFirstTime = data.joinFirstTime != null ? StyledChatUtils.parseText(data.joinFirstTime) : null;
        this.left = data.left != null ? StyledChatUtils.parseText(data.left) : null;
        this.death = data.death != null ? StyledChatUtils.parseText(data.death) : null;
        this.advancementTask = data.advancementTask != null ? StyledChatUtils.parseText(data.advancementTask) : null;
        this.advancementChallenge = data.advancementChallenge != null ? StyledChatUtils.parseText(data.advancementChallenge) : null;
        this.advancementGoal = data.advancementGoal != null ? StyledChatUtils.parseText(data.advancementGoal) : null;
        this.privateMessageSent = data.privateMessageSent != null ? StyledChatUtils.parseText(data.privateMessageSent) : null;
        this.privateMessageReceived = data.privateMessageReceived != null ? StyledChatUtils.parseText(data.privateMessageReceived) : null;
        this.teamChatSent = data.teamChatSent != null ? StyledChatUtils.parseText(data.teamChatSent) : null;
        this.teamChatReceived = data.teamChatReceived != null ? StyledChatUtils.parseText(data.teamChatReceived) : null;
        this.sayCommand = data.sayCommand != null ? StyledChatUtils.parseText(data.sayCommand) : null;
        this.meCommand = data.meCommand != null ? StyledChatUtils.parseText(data.meCommand) : null;
    }



    public Text getDisplayName(ServerPlayerEntity player, Text vanillaDisplayName) {
        if (this.displayName == null) {
            return null;
        } else if (this.displayName == StyledChatUtils.IGNORED_TEXT) {
            return vanillaDisplayName;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.displayName, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("vanillaDisplayName", vanillaDisplayName,
                    "player", vanillaDisplayName,
                    "default", vanillaDisplayName,
                        "name", player.getName())
        );
    }

    public Text getChat(ServerPlayerEntity player, Text message) {
        if (this.chat == null) {
            return null;
        } else if (this.chat == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.chat, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "message", message)
        );
    }

    public Text getJoin(ServerPlayerEntity player) {
        if (this.join == null) {
            return null;
        } else if (this.join == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.join, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    public Text getJoinFirstTime(ServerPlayerEntity player) {
        if (this.joinFirstTime == null) {
            return null;
        } else if (this.joinFirstTime == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.joinFirstTime, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    public Text getJoinRenamed(ServerPlayerEntity player, String oldName) {
        if (this.joinRenamed == null) {
            return null;
        } else if (this.joinRenamed == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.joinRenamed, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "old_name", new LiteralText(oldName))
        );
    }

    public Text getLeft(ServerPlayerEntity player) {
        if (this.left == null) {
            return null;
        } else if (this.left == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.left, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    public Text getDeath(ServerPlayerEntity player, Text vanillaMessage) {
        if (this.death == null) {
            return null;
        } else if (this.death == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.death, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "default_message", vanillaMessage)
        );
    }

    public Text getAdvancementGoal(ServerPlayerEntity player, Text advancement) {
        if (this.advancementGoal == null) {
            return null;
        } else if (this.advancementGoal == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.advancementGoal, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    public Text getAdvancementTask(ServerPlayerEntity player, Text advancement) {
        if (this.advancementTask == null) {
            return null;
        }else if (this.advancementTask == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.advancementTask, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    public Text getAdvancementChallenge(ServerPlayerEntity player, Text advancement) {
        if (this.advancementChallenge == null) {
            return null;
        }else if (this.advancementChallenge == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.advancementChallenge, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    public Text getSayCommand(ServerCommandSource source, Text message) {
        if (this.sayCommand == null) {
            return null;
        } else if (this.sayCommand == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        try {
            var player = source.getPlayer();
            return PlaceholderAPI.parsePredefinedText(
                    PlaceholderAPI.parseText(this.sayCommand, player),
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", player.getDisplayName(),
                            "displayName", source.getDisplayName(),
                            "message", message)
            );
        } catch (Exception e) {
            return PlaceholderAPI.parsePredefinedText(
                    PlaceholderAPI.parseText(this.sayCommand, source.getServer()),
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", source.getDisplayName(),
                            "displayName", source.getDisplayName(),
                            "message", message)
            );
        }
    }

    public Text getMeCommand(ServerCommandSource source, Text message) {
        if (this.meCommand == null) {
            return null;
        } else if (this.meCommand == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        try {
            var player = source.getPlayer();
            return PlaceholderAPI.parsePredefinedText(
                    PlaceholderAPI.parseText(this.meCommand, player),
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", player.getDisplayName(),
                            "displayName", source.getDisplayName(),
                            "message", message)
            );
        } catch (Exception e) {
            return PlaceholderAPI.parsePredefinedText(
                    PlaceholderAPI.parseText(this.meCommand, source.getServer()),
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", source.getDisplayName(),
                            "displayName", source.getDisplayName(),
                            "message", message)
            );
        }
    }

    public Text getPrivateMessageSent(Text sender, Text receiver, Text message, Object placeholderContext) {
        if (this.privateMessageSent == null) {
            return null;
        } else if (this.privateMessageSent == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        var template = placeholderContext instanceof ServerPlayerEntity player
                ? PlaceholderAPI.parseText(this.privateMessageSent, player)
                : PlaceholderAPI.parseText(this.privateMessageSent, (MinecraftServer) placeholderContext);

        return PlaceholderAPI.parsePredefinedText(
                template,
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("sender", sender,
                        "receiver", receiver,
                        "message", message)
        );
    }

    public Text getPrivateMessageReceived(Text sender, Text receiver, Text message, Object placeholderContext) {
        if (this.privateMessageReceived == null) {
            return null;
        } else if (this.privateMessageReceived == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        var template = placeholderContext instanceof ServerPlayerEntity player
                ? PlaceholderAPI.parseText(this.privateMessageReceived, player)
                : PlaceholderAPI.parseText(this.privateMessageReceived, (MinecraftServer) placeholderContext);

        return PlaceholderAPI.parsePredefinedText(
                template,
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("sender", sender,
                        "receiver", receiver,
                        "message", message)
        );
    }

    public Text getTeamChatSent(Text team, Text displayName, Text message, Object placeholderContext) {
        if (this.teamChatSent == null) {
            return null;
        } else if (this.teamChatSent == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        var template = placeholderContext instanceof ServerPlayerEntity player
                ? PlaceholderAPI.parseText(this.teamChatSent, player)
                : PlaceholderAPI.parseText(this.teamChatSent, (MinecraftServer) placeholderContext);

        return PlaceholderAPI.parsePredefinedText(
                template,
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("team", team,
                        "displayName", displayName,
                        "message", message)
        );
    }

    public Text getTeamChatReceived(Text team, Text displayName, Text message, Object placeholderContext) {
        if (this.teamChatReceived == null) {
            return null;
        } else if (this.teamChatReceived == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        var template = placeholderContext instanceof ServerPlayerEntity player
                ? PlaceholderAPI.parseText(this.teamChatReceived, player)
                : PlaceholderAPI.parseText(this.teamChatReceived, (MinecraftServer) placeholderContext);

        return PlaceholderAPI.parsePredefinedText(
                template,
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("team", team,
                        "displayName", displayName,
                        "message", message)
        );
    }
}
