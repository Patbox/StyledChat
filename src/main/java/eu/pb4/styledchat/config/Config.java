package eu.pb4.styledchat.config;


import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.styledchat.config.data.ChatStyleData;
import eu.pb4.styledchat.config.data.ConfigData;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Config {
    public final ConfigData configData;
    private final ChatStyle defaultStyle;
    private final List<ChatStyle> permissionStyle;
    public final Set<String> allPossibleAutoCompletionKeys;

    public Config(ConfigData data) {
        this.configData = data;
        this.defaultStyle = new ChatStyle(data.defaultStyle, new ChatStyle(ChatStyleData.DEFAULT));

        this.permissionStyle = new ArrayList<>();

        this.allPossibleAutoCompletionKeys = new HashSet<>();

        for (var entry : data.permissionStyles) {
            if (entry.require == null) {
                entry.require = BuiltinPredicates.operatorLevel(4);
            }

            this.permissionStyle.add(new ChatStyle(entry));

            for (var key : entry.emoticons.keySet()) {
                this.allPossibleAutoCompletionKeys.add(":" + key + ":");
            }

        }


        for (var tag : TextParserV1.DEFAULT.getTags()) {
            this.allPossibleAutoCompletionKeys.add("<" + tag.name() + ">");
            if (tag.aliases() != null) {
                for (var a : tag.aliases()) {
                    this.allPossibleAutoCompletionKeys.add("<" + a + ">");
                }
            }
        }
    }

    public Text getDisplayName(ServerPlayerEntity player, Text vanillaDisplayName) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getDisplayName(player, vanillaDisplayName);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getDisplayName(player, vanillaDisplayName);
    }

    public Text getChat(ServerPlayerEntity player, Text message) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getChat(player, message);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getChat(player, message);
    }

    public Text getJoin(ServerPlayerEntity player) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getJoin(player);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getJoin(player);
    }

    public Text getJoinFirstTime(ServerPlayerEntity player) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getJoinFirstTime(player);
                if (text != null) {
                    return text;
                }
            }
        }
        Text text = this.defaultStyle.getJoinFirstTime(player);
        if (text != null) {
            return text;
        }
        return this.getJoin(player);
    }

    public Text getJoinRenamed(ServerPlayerEntity player, String oldName) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getJoinRenamed(player, oldName);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getJoinRenamed(player, oldName);
    }

    public Text getLeft(ServerPlayerEntity player) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getLeft(player);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getLeft(player);
    }

    public Text getDeath(ServerPlayerEntity player, Text vanillaMessage) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getDeath(player, vanillaMessage);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getDeath(player, vanillaMessage);
    }

    public Text getAdvancementTask(ServerPlayerEntity player, Text advancement) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getAdvancementTask(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementTask(player, advancement);
    }

    public Text getAdvancementGoal(ServerPlayerEntity player, Text advancement) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getAdvancementGoal(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementGoal(player, advancement);
    }

    public Text getAdvancementChallenge(ServerPlayerEntity player, Text advancement) {
        var context = PredicateContext.of(player);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getAdvancementChallenge(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementChallenge(player, advancement);
    }

    public Text getSayCommand(ServerCommandSource source, Text message) {
        var context = PredicateContext.of(source);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getSayCommand(source, message);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getSayCommand(source, message);
    }

    public Text getMeCommand(ServerCommandSource source, Text message) {
        var context = PredicateContext.of(source);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context).success()) {
                Text text = entry.getMeCommand(source, message);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getMeCommand(source, message);
    }

    public Text getPrivateMessageSent(Text sender, Text receiver, Text message, ServerCommandSource context) {
        var placeholderContext = PlaceholderContext.of(context);

        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Text text = entry.getPrivateMessageSent(sender, receiver, message, placeholderContext);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getPrivateMessageSent(sender, receiver, message, placeholderContext);
    }

    public Text getPrivateMessageReceived(Text sender, Text receiver, Text message, ServerCommandSource context) {
        var placeholderContext = PlaceholderContext.of(context);

        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Text text = entry.getPrivateMessageReceived(sender, receiver, message, placeholderContext);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getPrivateMessageReceived(sender, receiver, message, placeholderContext);
    }

    public Text getTeamChatSent(Text team, Text displayName, Text message, ServerCommandSource context) {
        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Text text = entry.getTeamChatSent(team, displayName, message, context);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getTeamChatSent(team, displayName, message, context);
    }

    public Text getTeamChatReceived(Text team, Text displayName, Text message, ServerCommandSource context) {
        var context2 = PredicateContext.of(context);
        for (var entry : this.permissionStyle) {
            if (entry.require.test(context2).success()) {
                Text text = entry.getTeamChatReceived(team, displayName, message, context);
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

    public Text getPetDeath(TameableEntity entity, Text vanillaMessage) {
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

    public Map<String, TextNode> getEmotes(ServerCommandSource source) {
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

    public Object2BooleanOpenHashMap<String> getAllowedFormatting(ServerCommandSource source) {
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
    public Text getCustom(Identifier identifier, Text displayName, Text message, @Nullable Text receiver, ServerCommandSource source) {
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
