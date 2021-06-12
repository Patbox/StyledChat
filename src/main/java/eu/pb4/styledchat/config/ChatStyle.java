package eu.pb4.styledchat.config;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.config.data.ChatStyleData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Map;

public class ChatStyle {
    public final Text displayName;
    public final Text chat;
    public final Text join;
    public final Text joinRenamed;
    public final Text left;
    public final Text death;
    public final Text advancementTask;
    public final Text advancementChallenge;
    public final Text advancementGoal;

    public ChatStyle(ChatStyleData data, ChatStyle defaultStyle) {
        this.displayName = data.displayName != null ? TextParser.parse(data.displayName.replace("%player:displayname%", "")) : defaultStyle.displayName;
        this.chat = data.chat != null ? TextParser.parse(data.chat) : defaultStyle.chat;
        this.join = data.join != null ? TextParser.parse(data.join) : defaultStyle.join;
        this.joinRenamed = data.joinRenamed != null ? TextParser.parse(data.joinRenamed) : defaultStyle.joinRenamed;
        this.left = data.left != null ? TextParser.parse(data.left) : defaultStyle.left;
        this.death = data.death != null ? TextParser.parse(data.death) : defaultStyle.death;
        this.advancementTask = data.advancementTask != null ? TextParser.parse(data.advancementTask) : defaultStyle.advancementTask;
        this.advancementChallenge = data.advancementChallenge != null ? TextParser.parse(data.advancementChallenge) : defaultStyle.advancementChallenge;
        this.advancementGoal = data.advancementGoal != null ? TextParser.parse(data.advancementGoal) : defaultStyle.advancementGoal;

    }

    public ChatStyle(ChatStyleData data) {
        this.displayName = data.displayName != null ? TextParser.parse(data.displayName.replace("%player:displayname%", "")) : null;
        this.chat = data.chat != null ? TextParser.parse(data.chat) : null;
        this.join = data.join != null ? TextParser.parse(data.join) : null;
        this.joinRenamed = data.joinRenamed != null ? TextParser.parse(data.joinRenamed) : null;
        this.left = data.left != null ? TextParser.parse(data.left) : null;
        this.death = data.death != null ? TextParser.parse(data.death) : null;
        this.advancementTask = data.advancementTask != null ? TextParser.parse(data.advancementTask) : null;
        this.advancementChallenge = data.advancementChallenge != null ? TextParser.parse(data.advancementChallenge) : null;
        this.advancementGoal = data.advancementGoal != null ? TextParser.parse(data.advancementGoal) : null;

    }

    public Text getDisplayName(ServerPlayerEntity player, Text vanillaDisplayName) {
        if (this.displayName == null) {
            return null;
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.displayName, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("vanillaDisplayName", vanillaDisplayName,
                        "name", player.getName())
        );
    }

    public Text getChat(ServerPlayerEntity player, Text message) {
        if (this.chat == null) {
            return null;
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
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.join, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    public Text getJoinRenamed(ServerPlayerEntity player, String oldName) {
        if (this.joinRenamed == null) {
            return null;
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
        }

        return PlaceholderAPI.parsePredefinedText(
                PlaceholderAPI.parseText(this.advancementChallenge, player),
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }
}
