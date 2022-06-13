package eu.pb4.styledchat.config;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.data.ChatStyleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class ChatStyle {
    public final TextNode displayName;
    public final TextNode chat;
    public final TextNode join;
    public final TextNode joinFirstTime;
    public final TextNode joinRenamed;
    public final TextNode left;
    public final TextNode death;
    public final TextNode advancementTask;
    public final TextNode advancementChallenge;
    public final TextNode advancementGoal;
    public final TextNode privateMessageSent;
    public final TextNode privateMessageReceived;
    public final TextNode teamChatSent;
    public final TextNode teamChatReceived;
    public final TextNode sayCommand;
    public final TextNode meCommand;

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
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(
                this.displayName,
                context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(
                this.chat,
                context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(
                this.join,
                context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    public Text getJoinFirstTime(ServerPlayerEntity player) {
        if (this.joinFirstTime == null) {
            return null;
        } else if (this.joinFirstTime == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.joinFirstTime,
                context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    public Text getJoinRenamed(ServerPlayerEntity player, String oldName) {
        if (this.joinRenamed == null) {
            return null;
        } else if (this.joinRenamed == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.joinRenamed, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "old_name", Text.literal(oldName))
        );
    }

    public Text getLeft(ServerPlayerEntity player) {
        if (this.left == null) {
            return null;
        } else if (this.left == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.left, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    public Text getDeath(ServerPlayerEntity player, Text vanillaMessage) {
        if (this.death == null) {
            return null;
        } else if (this.death == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.death, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.advancementGoal, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    public Text getAdvancementTask(ServerPlayerEntity player, Text advancement) {
        if (this.advancementTask == null) {
            return null;
        } else if (this.advancementTask == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.advancementTask, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    public Text getAdvancementChallenge(ServerPlayerEntity player, Text advancement) {
        if (this.advancementChallenge == null) {
            return null;
        } else if (this.advancementChallenge == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.advancementChallenge, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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
            return Placeholders.parseText(
                    Placeholders.parseText(this.sayCommand, PlaceholderContext.of(player)),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", player.getDisplayName(),
                            "displayName", source.getDisplayName(),
                            "message", message)
            );
        } catch (Exception e) {
            return Placeholders.parseText(
                    Placeholders.parseText(this.sayCommand, PlaceholderContext.of(source.getServer())),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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
            return Placeholders.parseText(
                    Placeholders.parseText(this.meCommand, PlaceholderContext.of(player)),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", player.getDisplayName(),
                            "displayName", source.getDisplayName(),
                            "message", message)
            );
        } catch (Exception e) {
            return Placeholders.parseText(
                    Placeholders.parseText(this.meCommand, PlaceholderContext.of(source.getServer())),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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

        return Placeholders.parseText(
                this.privateMessageSent,
                placeholderContext instanceof ServerPlayerEntity player ? PlaceholderContext.of(player) : PlaceholderContext.of((MinecraftServer) placeholderContext),
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("sender", sender,
                        "receiver", receiver,
                        "message", message)
        );
    }

    public Text getPrivateMessageReceived(Text sender, Text receiver, Text message, PlaceholderContext context) {
        if (this.privateMessageReceived == null) {
            return null;
        } else if (this.privateMessageReceived == StyledChatUtils.IGNORED_TEXT) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return Placeholders.parseText(
                this.privateMessageReceived,
                context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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

        return Placeholders.parseText(
                this.teamChatSent,
                placeholderContext instanceof ServerPlayerEntity player ? PlaceholderContext.of(player) : PlaceholderContext.of((MinecraftServer) placeholderContext),

                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
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

        return Placeholders.parseText(
                this.teamChatReceived,
                placeholderContext instanceof ServerPlayerEntity player ? PlaceholderContext.of(player) : PlaceholderContext.of((MinecraftServer) placeholderContext),
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("team", team,
                        "displayName", displayName,
                        "message", message)
        );
    }
}
