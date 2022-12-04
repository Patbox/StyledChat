package eu.pb4.styledchat.config;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.EmptyNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.predicate.api.PredicateRegistry;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.data.ChatStyleData;
import eu.pb4.styledchat.config.data.ConfigData;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ChatStyle {
    public static final ChatStyle EMPTY = new ChatStyle(new ChatStyleData());
    public final MinecraftPredicate require;

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

    public final TextNode petDeath;

    public final TextNode spoilerStyle;
    public final String spoilerSymbol;
    public final TextNode linkStyle;
    public final TextNode mentionStyle;
    public final Map<String, TextNode> emoticons = new HashMap<>();
    public final Object2BooleanMap<String> formatting = new Object2BooleanOpenHashMap<>();


    public ChatStyle(ChatStyleData data, ChatStyle defaultStyle) {
        this.require = data instanceof ConfigData.RequireChatStyleData data1 ? data1.require : BuiltinPredicates.operatorLevel(0);

        this.displayName = data.displayName != null ? StyledChatUtils.parseText(data.displayName.replace("%player:displayname%", "")) : defaultStyle.displayName;

        this.chat = data.messages.chat != null ? StyledChatUtils.parseText(data.messages.chat) : defaultStyle.chat;
        this.join = data.messages.joinedGame != null ? StyledChatUtils.parseText(data.messages.joinedGame) : defaultStyle.join;
        this.joinFirstTime = data.messages.joinedForFirstTime != null ? StyledChatUtils.parseText(data.messages.joinedForFirstTime) : this.join;
        this.joinRenamed = data.messages.joinedAfterNameChange != null ? StyledChatUtils.parseText(data.messages.joinedAfterNameChange) : defaultStyle.joinRenamed;
        this.left = data.messages.leftGame != null ? StyledChatUtils.parseText(data.messages.leftGame) : defaultStyle.left;
        this.death = data.messages.baseDeath != null ? StyledChatUtils.parseText(data.messages.baseDeath) : defaultStyle.death;
        this.advancementTask = data.messages.advancementTask != null ? StyledChatUtils.parseText(data.messages.advancementTask) : defaultStyle.advancementTask;
        this.advancementChallenge = data.messages.advancementChallenge != null ? StyledChatUtils.parseText(data.messages.advancementChallenge) : defaultStyle.advancementChallenge;
        this.advancementGoal = data.messages.advancementGoal != null ? StyledChatUtils.parseText(data.messages.advancementGoal) : defaultStyle.advancementGoal;
        this.privateMessageSent = data.messages.privateMessageSent != null ? StyledChatUtils.parseText(data.messages.privateMessageSent) : defaultStyle.privateMessageSent;
        this.privateMessageReceived = data.messages.privateMessageReceived != null ? StyledChatUtils.parseText(data.messages.privateMessageReceived) : defaultStyle.privateMessageReceived;
        this.teamChatSent = data.messages.sentTeamChat != null ? StyledChatUtils.parseText(data.messages.sentTeamChat) : defaultStyle.teamChatSent;
        this.teamChatReceived = data.messages.receivedTeamChat != null ? StyledChatUtils.parseText(data.messages.receivedTeamChat) : defaultStyle.teamChatReceived;
        this.sayCommand = data.messages.sayCommandMessage != null ? StyledChatUtils.parseText(data.messages.sayCommandMessage) : defaultStyle.sayCommand;
        this.meCommand = data.messages.meCommandMessage != null ? StyledChatUtils.parseText(data.messages.meCommandMessage) : defaultStyle.meCommand;
        this.petDeath = data.messages.petDeathMessage != null ? StyledChatUtils.parseText(data.messages.petDeathMessage) : defaultStyle.petDeath;

        this.spoilerStyle = data.spoilerStyle != null ? StyledChatUtils.parseText(data.spoilerStyle) : defaultStyle.spoilerStyle;
        this.spoilerSymbol = data.spoilerSymbol != null ? data.spoilerSymbol : defaultStyle.spoilerSymbol;
        this.linkStyle = data.linkStyle != null ? StyledChatUtils.parseText(data.linkStyle) : defaultStyle.linkStyle;
        this.mentionStyle = data.mentionStyle != null ? StyledChatUtils.parseText(data.mentionStyle) : defaultStyle.mentionStyle;

        for (var emoticon : data.emoticons.entrySet()) {
            this.emoticons.put(emoticon.getKey(), StyledChatUtils.parseText(emoticon.getValue()));
        }

        for (var formatting : data.formatting.entrySet()) {
            this.formatting.put(formatting.getKey(), formatting.getValue().booleanValue());
        }
    }

    public ChatStyle(ChatStyleData data) {
        this.require = data instanceof ConfigData.RequireChatStyleData data1 ? data1.require : BuiltinPredicates.operatorLevel(0);

        this.displayName = data.displayName != null ? StyledChatUtils.parseText(data.displayName.replace("%player:displayname%", "")) : null;
        this.chat = data.messages.chat != null ? StyledChatUtils.parseText(data.messages.chat) : null;
        this.join = data.messages.joinedGame != null ? StyledChatUtils.parseText(data.messages.joinedGame) : null;
        this.joinRenamed = data.messages.joinedAfterNameChange != null ? StyledChatUtils.parseText(data.messages.joinedAfterNameChange) : null;
        this.joinFirstTime = data.messages.joinedForFirstTime != null ? StyledChatUtils.parseText(data.messages.joinedForFirstTime) : null;
        this.left = data.messages.leftGame != null ? StyledChatUtils.parseText(data.messages.leftGame) : null;
        this.death = data.messages.baseDeath != null ? StyledChatUtils.parseText(data.messages.baseDeath) : null;
        this.advancementTask = data.messages.advancementTask != null ? StyledChatUtils.parseText(data.messages.advancementTask) : null;
        this.advancementChallenge = data.messages.advancementChallenge != null ? StyledChatUtils.parseText(data.messages.advancementChallenge) : null;
        this.advancementGoal = data.messages.advancementGoal != null ? StyledChatUtils.parseText(data.messages.advancementGoal) : null;
        this.privateMessageSent = data.messages.privateMessageSent != null ? StyledChatUtils.parseText(data.messages.privateMessageSent) : null;
        this.privateMessageReceived = data.messages.privateMessageReceived != null ? StyledChatUtils.parseText(data.messages.privateMessageReceived) : null;
        this.teamChatSent = data.messages.sentTeamChat != null ? StyledChatUtils.parseText(data.messages.sentTeamChat) : null;
        this.teamChatReceived = data.messages.receivedTeamChat != null ? StyledChatUtils.parseText(data.messages.receivedTeamChat) : null;
        this.sayCommand = data.messages.sayCommandMessage != null ? StyledChatUtils.parseText(data.messages.sayCommandMessage) : null;
        this.meCommand = data.messages.meCommandMessage != null ? StyledChatUtils.parseText(data.messages.meCommandMessage) : null;
        this.petDeath = data.messages.petDeathMessage != null ? StyledChatUtils.parseText(data.messages.petDeathMessage) : null;

        this.spoilerStyle = data.spoilerStyle != null ? StyledChatUtils.parseText(data.spoilerStyle) : null;
        this.spoilerSymbol = data.spoilerSymbol != null ? data.spoilerSymbol : null;
        this.linkStyle = data.linkStyle != null ? StyledChatUtils.parseText(data.linkStyle) : null;
        this.mentionStyle = data.mentionStyle != null ? StyledChatUtils.parseText(data.mentionStyle) : null;

        for (var emoticon : data.emoticons.entrySet()) {
            this.emoticons.put(emoticon.getKey(), StyledChatUtils.parseText(emoticon.getValue()));
        }

        for (var formatting : data.formatting.entrySet()) {
            this.formatting.put(formatting.getKey(), formatting.getValue().booleanValue());
        }
    }


    public Text getDisplayName(ServerPlayerEntity player, Text vanillaDisplayName) {
        if (this.displayName == null) {
            return null;
        } else if (this.displayName == EmptyNode.INSTANCE) {
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

    @Nullable
    public Text getChat(ServerPlayerEntity player, Text message) {
        if (this.chat == null) {
            return null;
        } else if (this.chat == EmptyNode.INSTANCE) {
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

    @Nullable
    public Text getJoin(ServerPlayerEntity player) {
        if (this.join == null) {
            return null;
        } else if (this.join == EmptyNode.INSTANCE) {
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

    @Nullable
    public Text getJoinFirstTime(ServerPlayerEntity player) {
        if (this.joinFirstTime == null) {
            return null;
        } else if (this.joinFirstTime == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.joinFirstTime,
                context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    @Nullable
    public Text getJoinRenamed(ServerPlayerEntity player, String oldName) {
        if (this.joinRenamed == null) {
            return null;
        } else if (this.joinRenamed == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.joinRenamed, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "old_name", Text.literal(oldName))
        );
    }

    @Nullable
    public Text getLeft(ServerPlayerEntity player) {
        if (this.left == null) {
            return null;
        } else if (this.left == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.left, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName())
        );
    }

    @Nullable
    public Text getDeath(ServerPlayerEntity player, Text vanillaMessage) {
        if (this.death == null) {
            return null;
        } else if (this.death == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.death, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "default_message", vanillaMessage)
        );
    }

    @Nullable
    public Text getAdvancementGoal(ServerPlayerEntity player, Text advancement) {
        if (this.advancementGoal == null) {
            return null;
        } else if (this.advancementGoal == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.advancementGoal, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    @Nullable
    public Text getAdvancementTask(ServerPlayerEntity player, Text advancement) {
        if (this.advancementTask == null) {
            return null;
        } else if (this.advancementTask == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.advancementTask, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    @Nullable
    public Text getAdvancementChallenge(ServerPlayerEntity player, Text advancement) {
        if (this.advancementChallenge == null) {
            return null;
        } else if (this.advancementChallenge == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }
        var context = PlaceholderContext.of(player);

        return Placeholders.parseText(this.advancementChallenge, context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("player", player.getDisplayName(),
                        "advancement", advancement)
        );
    }

    @Nullable
    public Text getSayCommand(ServerCommandSource source, Text message) {
        if (this.sayCommand == null) {
            return null;
        } else if (this.sayCommand == EmptyNode.INSTANCE) {
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

    @Nullable
    public Text getMeCommand(ServerCommandSource source, Text message) {
        if (this.meCommand == null) {
            return null;
        } else if (this.meCommand == EmptyNode.INSTANCE) {
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

    @Nullable
    public Text getPrivateMessageSent(Text sender, Text receiver, Text message, PlaceholderContext context) {
        if (this.privateMessageSent == null) {
            return null;
        } else if (this.privateMessageSent == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return Placeholders.parseText(
                this.privateMessageSent,
                context,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("sender", sender,
                        "receiver", receiver,
                        "message", message)
        );
    }

    @Nullable
    public Text getPrivateMessageReceived(Text sender, Text receiver, Text message, PlaceholderContext context) {
        if (this.privateMessageReceived == null) {
            return null;
        } else if (this.privateMessageReceived == EmptyNode.INSTANCE) {
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

    @Nullable
    public Text getTeamChatSent(Text team, Text displayName, Text message, ServerCommandSource context) {
        if (this.teamChatSent == null) {
            return null;
        } else if (this.teamChatSent == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return Placeholders.parseText(
                this.teamChatSent,
                PlaceholderContext.of(context),
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("team", team,
                        "displayName", displayName,
                        "message", message)
        );
    }

    @Nullable
    public Text getTeamChatReceived(Text team, Text displayName, Text message, ServerCommandSource context) {
        if (this.teamChatReceived == null) {
            return null;
        } else if (this.teamChatReceived == EmptyNode.INSTANCE) {
            return StyledChatUtils.IGNORED_TEXT;
        }

        return Placeholders.parseText(
                this.teamChatReceived,
                PlaceholderContext.of(context),
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("team", team,
                        "displayName", displayName,
                        "message", message)
        );
    }

    @Nullable
    public TextNode getLink() {
        return this.linkStyle;
    }

    @Nullable
    public TextNode getMention() {
        return this.mentionStyle;
    }

    @Nullable
    public TextNode getSpoilerStyle() {
        return this.spoilerStyle;
    }

    @Nullable
    public String getSpoilerSymbol() {
        return this.spoilerSymbol;
    }

    public Text getPetDeath(TameableEntity entity, Text vanillaMessage) {
        if (this.petDeath == null) {
            return null;
        }

            return Placeholders.parseText(
                    this.petDeath,
                    PlaceholderContext.of(entity),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("pet", entity.getDisplayName(),
                            "default_message", vanillaMessage)
            );
    }
}
