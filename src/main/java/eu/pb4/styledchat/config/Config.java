package eu.pb4.styledchat.config;


import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.styledchat.config.data.ChatStyleData;
import eu.pb4.styledchat.config.data.ConfigData;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;

public final class Config {
    public static final Config DEFAULT = new Config(new ConfigData());
    public final ConfigData configData;
    private final ChatStyle defaultStyle;
    private final List<ChatStyle> permissionStyle;
    public final Set<String> allPossibleAutoCompletionKeys;

    public Config(ConfigData data) {
        this.configData = data;
        this.defaultStyle = new ChatStyle(data.defaultStyle, new ChatStyle(ChatStyleData.DEFAULT));
        this.permissionStyle = new ArrayList<>();

        this.allPossibleAutoCompletionKeys = new HashSet<>();

        for (var key : this.defaultStyle.emoticons.keySet()) {
            this.allPossibleAutoCompletionKeys.add(":" + key + ":");
        }

        for (var entry : data.permissionStyles) {
            if (entry.require == null) {
                entry.require = BuiltinPredicates.operatorLevel(4);
            }

            var style = new ChatStyle(entry);
            this.permissionStyle.add(style);

            for (var key : style.emoticons.keySet()) {
                this.allPossibleAutoCompletionKeys.add(":" + key + ":");
            }
        }


        for (var tag : TagRegistry.DEFAULT.getTags()) {
            this.allPossibleAutoCompletionKeys.add("<" + tag.name() + ">");
            if (tag.aliases() != null) {
                for (var a : tag.aliases()) {
                    this.allPossibleAutoCompletionKeys.add("<" + a + ">");
                }
            }
        }
    }

    public Component getDisplayName(ServerPlayer player, Component vanillaDisplayName) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getDisplayName(player, vanillaDisplayName);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getDisplayName(player, vanillaDisplayName);
    }

    public Component getChat(ServerPlayer player, Component message) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getChat(player, message);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getChat(player, message);
    }

    public Component getJoin(ServerPlayer player) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getJoin(player);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getJoin(player);
    }

    public Component getJoinFirstTime(ServerPlayer player) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getJoinFirstTime(player);
                if (text != null) {
                    return text;
                }
            }
        }
        Component text = this.defaultStyle.getJoinFirstTime(player);
        if (text != null) {
            return text;
        }
        return this.getJoin(player);
    }

    public Component getJoinRenamed(ServerPlayer player, String oldName) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getJoinRenamed(player, oldName);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getJoinRenamed(player, oldName);
    }

    public Component getLeft(ServerPlayer player) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getLeft(player);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getLeft(player);
    }

    public Component getDeath(ServerPlayer player, Component vanillaMessage) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getDeath(player, vanillaMessage);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getDeath(player, vanillaMessage);
    }

    public Component getAdvancementTask(ServerPlayer player, Component advancement) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getAdvancementTask(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementTask(player, advancement);
    }

    public Component getAdvancementGoal(ServerPlayer player, Component advancement) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getAdvancementGoal(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementGoal(player, advancement);
    }

    public Component getAdvancementChallenge(ServerPlayer player, Component advancement) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getAdvancementChallenge(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementChallenge(player, advancement);
    }

    public Component getSayCommand(CommandSourceStack source, Component message) {
        var context = PredicateContext.of(source);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getSayCommand(source, message);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getSayCommand(source, message);
    }

    public Component getMeCommand(CommandSourceStack source, Component message) {
        var context = PredicateContext.of(source);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Component text = entry.getMeCommand(source, message);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getMeCommand(source, message);
    }

    public Component getPrivateMessageSent(Component sender, Component receiver, Component message, CommandSourceStack context) {
        var placeholderContext = PlaceholderContext.of(context);

        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Component text = entry.getPrivateMessageSent(sender, receiver, message, placeholderContext);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getPrivateMessageSent(sender, receiver, message, placeholderContext);
    }

    public Component getPrivateMessageReceived(Component sender, Component receiver, Component message, CommandSourceStack context) {
        var placeholderContext = PlaceholderContext.of(context);

        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Component text = entry.getPrivateMessageReceived(sender, receiver, message, placeholderContext);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getPrivateMessageReceived(sender, receiver, message, placeholderContext);
    }

    public Component getTeamChatSent(Component team, Component displayName, Component message, CommandSourceStack context) {
        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Component text = entry.getTeamChatSent(team, displayName, message, context);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getTeamChatSent(team, displayName, message, context);
    }

    public Component getTeamChatReceived(Component team, Component displayName, Component message, CommandSourceStack context) {
        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Component text = entry.getTeamChatReceived(team, displayName, message, context);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getTeamChatReceived(team, displayName, message, context);
    }

    public TextNode getSpoilerStyle(PlaceholderContext ctx) {
        var context2 = PredicateContext.of(ctx.source());
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                var text = entry.getSpoilerStyle();
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getSpoilerStyle();
    }

    public String getSpoilerSymbole(PlaceholderContext ctx) {
        var context2 = PredicateContext.of(ctx.source());
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                var text = entry.getSpoilerSymbol();
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getSpoilerSymbol();
    }

    public TextNode getLinkStyle(PlaceholderContext ctx) {
        var context2 = PredicateContext.of(ctx.source());
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                var text = entry.getLink();
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getLink();
    }

    public TextNode getMentionStyle(PlaceholderContext ctx) {
        var context2 = PredicateContext.of(ctx.source());
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                var text = entry.getMention();
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getMention();
    }

    public Component getPetDeath(TamableAnimal entity, Component vanillaMessage) {
        var context2 = PredicateContext.of(entity);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                var text = this.defaultStyle.getPetDeath(entity, vanillaMessage);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getPetDeath(entity, vanillaMessage);
    }

    public Map<String, TextNode> getEmotes(CommandSourceStack source) {
        var base = new HashMap<>(this.defaultStyle.emoticons);
        var context = PredicateContext.of(source);

        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                for (var emoticon : entry.emoticons.entrySet()) {
                    if (!base.containsKey(emoticon.getKey())) {
                        base.put(emoticon.getKey(), emoticon.getValue());
                    }
                }
            }
        }

        return base;
    }

    public Object2BooleanOpenHashMap<String> getAllowedFormatting(CommandSourceStack source) {
        var base = new Object2BooleanOpenHashMap<>(this.defaultStyle.formatting);
        var context = PredicateContext.of(source);

        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                for (var formatting : entry.formatting.object2BooleanEntrySet()) {
                    if (!base.containsKey(formatting.getKey())) {
                        base.put(formatting.getKey(), formatting.getBooleanValue());
                    }
                }
            }
        }

        return base;
    }

    @Nullable
    public Component getCustom(Identifier identifier, Component displayName, Component message, @Nullable Component receiver, CommandSourceStack source) {
        var context2 = PredicateContext.of(source);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                var text = entry.getCustom(identifier, displayName, message, receiver, source);
                if (text != null) {
                    return text;
                }
            }
        }

        return this.defaultStyle.getCustom(identifier, displayName, message, receiver, source);
    }
}
