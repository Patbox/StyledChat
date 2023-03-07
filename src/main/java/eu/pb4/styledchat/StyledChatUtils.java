package eu.pb4.styledchat;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.EmptyNode;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.*;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.styledchat.config.ChatStyle;
import eu.pb4.styledchat.config.Config;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.config.data.ChatStyleData;
import eu.pb4.styledchat.config.data.VersionedChatStyleData;
import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.parser.LinkParser;
import eu.pb4.styledchat.parser.MentionParser;
import eu.pb4.styledchat.parser.SpoilerNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.EntitySelector;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public final class StyledChatUtils {
    public static final Text IGNORED_TEXT = Text.empty();

    public static final Pattern URL_REGEX = Pattern.compile("(https?:\\/\\/[-a-zA-Z0-9@:%._\\+~#=]+\\.[^ ]+)");

    public static final String ITEM_KEY = "item";
    public static final String POS_KEY = "pos";
    public static final String SPOILER_TAG = "spoiler";

    public static JsonDataStorage<VersionedChatStyleData> PLAYER_DATA = new JsonDataStorage<>("styled_chat_style", VersionedChatStyleData.class, ConfigManager.GSON);

    public static final TextParserV1.TagNodeBuilder SPOILER_TAG_HANDLER = (tag, data, input, handlers, endAt) -> {
        var out = TextParserV1.parseNodesWith(input, handlers, endAt);

        return new TextParserV1.TagNodeValue(new SpoilerNode(out.nodes()), out.length());
    };

    public static final TextParserV1.TextTag SPOILER_TEXT_TAG = TextParserV1.TextTag.of(SPOILER_TAG, List.of("hide"), "styledchat", true, SPOILER_TAG_HANDLER);


    public static final String FORMAT_PERMISSION_BASE = "styledchat.format.";
    public static final String FORMAT_PERMISSION_UNSAFE = "styledchat.unsafe_format.";
    public static final Pattern EMOTE_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))[:](?<id>[^:]+)[:]");;
    public static final Text EMPTY_TEXT = Text.empty();
    private static final Set<RegistryKey<MessageType>> DECORABLE = Set.of(MessageType.CHAT, MessageType.EMOTE_COMMAND, MessageType.MSG_COMMAND_INCOMING, MessageType.MSG_COMMAND_OUTGOING, MessageType.SAY_COMMAND, MessageType.TEAM_MSG_COMMAND_INCOMING, MessageType.TEAM_MSG_COMMAND_OUTGOING);

    public static TextNode parseText(String input) {
        return !input.isEmpty() ? Placeholders.parseNodes(TextParserUtils.formatNodes(input)) : EmptyNode.INSTANCE;
    }

    public static NodeParser createParser(ServerCommandSource source) {
        return createParser(PlaceholderContext.of(source));
    }

    public static NodeParser createParser(PlaceholderContext context) {
        var config = ConfigManager.getConfig();
        var list = new ArrayList<NodeParser>();
        var base = createTextParserV1(context.source());

        list.add(base);

        if (config.configData.formatting.parseLinksInChat) {
            list.add(new LinkParser(ConfigManager.getConfig().getLinkStyle(context)));
        }

        if (config.configData.formatting.parseMentionsInChat) {
            list.add(new MentionParser(ConfigManager.getConfig().getMentionStyle(context), context));
        }

        if (config.configData.formatting.markdown) {
            var form = new ArrayList<MarkdownLiteParserV1.MarkdownFormat>();

            if (base.getTagParser("bold") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.BOLD);
            }

            if (base.getTagParser("italic") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.ITALIC);
            }

            if (base.getTagParser("underline") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.UNDERLINE);
            }

            if (base.getTagParser("strikethrough") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.STRIKETHROUGH);
            }

            if (base.getTagParser(SPOILER_TAG) != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.SPOILER);
            }

            if (base.getTagParser("link") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.URL);
            }

            if (!form.isEmpty()) {
                list.add(new MarkdownLiteParserV1(SpoilerNode::new, MarkdownLiteParserV1::defaultQuoteFormatting, form.toArray(new MarkdownLiteParserV1.MarkdownFormat[0])));
            }
        }

        if (config.configData.formatting.legacyChatFormatting) {
            var form = new ArrayList<Formatting>();
            for (var formatting : Formatting.values()) {
                if (base.getTagParser(formatting.getName()) != null) {
                    form.add(formatting);
                }
            }

            boolean color = base.getTagParser("color") != null;

            if (!form.isEmpty() || color) {
                list.add(new LegacyFormattingParser(color, form.toArray(new Formatting[0])));
            }
        }

        var emotes = getEmotes(context);

        if (!emotes.isEmpty()) {
            list.add(new PatternPlaceholderParser(EMOTE_PATTERN, emotes::get));
        }

        return NodeParser.merge(list);
    }


    public static TextParserV1 createTextParserV1(ServerCommandSource source) {
        var parser = new TextParserV1();
        Config config = ConfigManager.getConfig();

        var allowedFormatting = config.getAllowedFormatting(source);

        for (var entry : TextParserV1.DEFAULT.getTags()) {
            if (allowedFormatting.getBoolean(entry.name())
                    || Permissions.check(source, (entry.userSafe() ? FORMAT_PERMISSION_BASE : FORMAT_PERMISSION_UNSAFE) + entry.name(), entry.userSafe() ? 2 : 4)
                    || Permissions.check(source, (entry.userSafe() ? FORMAT_PERMISSION_BASE : FORMAT_PERMISSION_UNSAFE) + ".type." + entry.type(), entry.userSafe() ? 2 : 4)
            ) {
                parser.register(entry);
            }
        }

        if (allowedFormatting.getBoolean(SPOILER_TAG)
                || Permissions.check(source, FORMAT_PERMISSION_BASE + SPOILER_TAG, 2)) {
            parser.register(SPOILER_TEXT_TAG);
        }

        StyledChatEvents.FORMATTING_CREATION_EVENT.invoker().onFormattingBuild(source, parser);

        return parser;
    }

    public static Map<String, TextNode> getEmotes(PlaceholderContext context) {
        return StyledChatStyles.getEmotes(context.hasPlayer() ? context.player().getCommandSource() : context.server().getCommandSource());
    }

    public static Text formatFor(PlaceholderContext context, String input) {
        var parser = createParser(context);
        var config = ConfigManager.getConfig();
        if (StyledChatMod.USE_FABRIC_API) {
            input = StyledChatEvents.PRE_MESSAGE_CONTENT.invoker().onPreMessage(input, context);
        }

        var value = TextNode.asSingle(parser.parseNodes(new LiteralNode(input)));

        if (StyledChatMod.USE_FABRIC_API) {
            value = StyledChatEvents.MESSAGE_CONTENT.invoker().onMessage(value, context);
        }

        var text = value.toText(context);

        if (config.configData.formatting.allowModdedDecorators) {
            try {
                text = context.server().getMessageDecorator().decorate(context.player(), text).get();
            } catch (Exception e) {
                // noop
            }
        }

        return text;
    }

    // Todo: Remove this
    public static String legacyFormatMessage(String input, Set<String> handlers) {
        var config = ConfigManager.getConfig();

        try {
            if (config.configData.formatting.markdown) {
                if (handlers.contains(SPOILER_TAG)) {
                    input = input.replaceAll(getMarkdownRegex("||", "\\|\\|"), "<spoiler>$2</spoiler>");
                }

                if (handlers.contains("bold")) {
                    input = input.replaceAll(getMarkdownRegex("**", "\\*\\*"), "<bold>$2</bold>");
                }

                if (handlers.contains("underline")) {
                    input = input.replaceAll(getMarkdownRegex("__", "__"), "<underline>$2</underline>");
                }

                if (handlers.contains("strikethrough")) {
                    input = input.replaceAll(getMarkdownRegex("~~", "~~"), "<strikethrough>$2</strikethrough>");
                }

                if (handlers.contains("italic")) {
                    input = input.replaceAll(getMarkdownRegex("*", "\\*"), "<italic>$2</italic>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return input;
    }

    private static String getMarkdownRegex(String base, String sides) {
        return "(" + sides + ")(?<id>[^" + base + "]+)(" + sides + ")";
    }

    public static MessageDecorator getChatDecorator() {
        return (player, message) -> {
            if (player != null) {
                return CompletableFuture.completedFuture(StyledChatStyles.getChat(player, formatFor(PlaceholderContext.of(player), message.getString())));
            } else {
                return CompletableFuture.completedFuture(formatFor(PlaceholderContext.of(StyledChatMod.server), message.getString()));
            }
        };
    }

    public static MessageDecorator getRawDecorator() {
        return (player, message) -> {
            if (player != null) {
                return CompletableFuture.completedFuture(formatFor(PlaceholderContext.of(player), message.getString()));
            } else {
                return CompletableFuture.completedFuture(formatFor(PlaceholderContext.of(StyledChatMod.server), message.getString()));
            }
        };
    }

    public static <T> MessageDecorator getCommandDecorator(String context, ServerCommandSource source, BiFunction<String, Class<?>, Object> argumentGetter) {
        Config config = ConfigManager.getConfig();


        return (player, message) -> {
            var input = formatFor(player != null ? PlaceholderContext.of(player) : PlaceholderContext.of(StyledChatMod.server), message.getString());


            return CompletableFuture.completedFuture(switch (context) {
                case "msg" -> {
                    try {
                        yield config.getPrivateMessageReceived(
                                source.getDisplayName(),
                                ((EntitySelector) argumentGetter.apply("targets", EntitySelector.class)).getPlayers(source).get(0).getDisplayName(),
                                input, source
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        yield Text.literal("");
                    }
                }
                case "teammsg" -> {
                    try {
                        yield config.getTeamChatReceived(((Team) source.getEntity().getScoreboardTeam()).getFormattedName(),
                                source.getDisplayName(),
                                input, source
                        );
                    } catch (Exception e) {
                        yield Text.literal("");
                    }
                }

                case "say" -> config.getSayCommand(source, input);

                case "me" -> config.getMeCommand(source, input);

                default -> input;
            });
        };
    }

    @Deprecated
    public static TextNode additionalParsing(TextNode node, PlaceholderContext context) {

        if (ConfigManager.getConfig().configData.formatting.parseLinksInChat) {
            node = parseLinks(node, context);
        }
        return node;
    }

    @Deprecated
    public static TextNode parseLinks(TextNode node, PlaceholderContext context) {
        return TextNode.asSingle(LinkParser.parse(node, context));
    }

    public static boolean isHandledByMod(RegistryKey<MessageType> typeKey) {
        return DECORABLE.contains(typeKey);
    }

    /*public static void modifyForSending(FilteredMessage<SignedMessage> message, ServerCommandSource source, RegistryKey<MessageType> type) {
        ((ExtSignedMessage) (Object) message.raw()).styledChat_setArg("override", StyledChatUtils.formatMessage(message.raw(), source, type));

        if (message.raw() != message.filtered()) {
            ((ExtSignedMessage) (Object) message.filtered()).styledChat_setArg("override", StyledChatUtils.formatMessage(message.filtered(), source, type));
        }
    }*/

    public static void modifyForSending(SignedMessage message, ServerCommandSource source, RegistryKey<MessageType> type) {
        try {
             ExtSignedMessage.setArg(message, "override", StyledChatUtils.formatMessage(message, source, type));
            ((ExtSignedMessage) (Object) message).styledChat_setType(type);
            ((ExtSignedMessage) (Object) message).styledChat_setSource(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Text formatMessage(SignedMessage message, ServerCommandSource source, RegistryKey<MessageType> type) {
        var ext = (ExtSignedMessage) (Object) message;

        var baseInput = ext.styledChat_getArg("base_input");

        var input = baseInput != null && baseInput.getContent() != TextContent.EMPTY
                ? baseInput
                : maybeFormatFor(source, ext.styledChat_getOriginal(), message.getContent());

        if (baseInput == null) {
            ext.styledChat_setArg("base_input", input);
        }

        return switch (type.getValue().getPath()) {
            case "msg_command_incoming" -> {
                try {
                    yield StyledChatStyles.getPrivateMessageReceived(
                            source.getDisplayName(),
                            ext.styledChat_getArg("targets"),
                            input, source
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    yield Text.empty();
                }
            }
            case "msg_command_outgoing" -> {
                try {
                    yield StyledChatStyles.getPrivateMessageSent(
                            source.getDisplayName(),
                            ext.styledChat_getArg("targets"),
                            input, source
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    yield Text.empty();
                }
            }
            case "team_msg_command_incoming" -> {
                try {
                    yield StyledChatStyles.getTeamChatReceived(((Team) source.getEntity().getScoreboardTeam()).getFormattedName(),
                            source.getDisplayName(),
                            input, source
                    );
                } catch (Exception e) {
                    yield Text.literal("");
                }
            }

            case "team_msg_command_outgoing" -> {
                try {
                    yield StyledChatStyles.getTeamChatSent(((Team) source.getEntity().getScoreboardTeam()).getFormattedName(),
                            source.getDisplayName(),
                            input, source
                    );
                } catch (Exception e) {
                    yield Text.literal("");
                }
            }
            case "say_command" -> StyledChatStyles.getSayCommand(source, input);

            case "emote_command" -> StyledChatStyles.getMeCommand(source, input);

            case "chat" -> StyledChatStyles.getChat(source.getPlayer(), input);

            default -> StyledChatStyles.getCustom(type.getValue(), source.getDisplayName(), input, null, source);
        };
    }

    public static Text maybeFormatFor(ServerCommandSource source, String original, Text originalContent) {
        return formatFor(source, original);
    }

    public static Text formatFor(ServerCommandSource source, String original) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            return formatFor(PlaceholderContext.of(player), original);
        } else {
            return formatFor(PlaceholderContext.of(source.getServer()), original);
        }
    }

    public static SignedMessage toEventMessage(SignedMessage message, PlaceholderContext context) {
        var ext = (ExtSignedMessage) (Object) message;

        var baseInput = ext.styledChat_getArg("base_input");
        var input = baseInput != null && baseInput.getContent() != TextContent.EMPTY ? baseInput : formatFor(context, ext.styledChat_getOriginal());
        if (baseInput == null) {
            ext.styledChat_setArg("base_input", input);
        }

        return new SignedMessage(message.link(), null, MessageBody.ofUnsigned(message.getSignedContent()), input, null);
    }

    public static void sendAutocompliton(ServerPlayerEntity player) {
        var config = ConfigManager.getConfig();
        player.networkHandler.sendPacket(new ChatSuggestionsS2CPacket(ChatSuggestionsS2CPacket.Action.REMOVE, new ArrayList<>(config.allPossibleAutoCompletionKeys)));

        var set = new HashSet<String>();

        var source = player.getCommandSource();

        var handler = StyledChatUtils.createTextParserV1(source);

        if (config.configData.autoCompletion.tags) {
            for (var tag : handler.getTags()) {
                set.add("<" + tag.name() + ">");

                if (config.configData.autoCompletion.tagAliases && tag.aliases() != null) {
                    for (var a : tag.aliases()) {
                        set.add("<" + a + ">");
                    }
                }
            }
        }
        if (config.configData.autoCompletion.emoticons) {
            for (var emote : config.getEmotes(source).keySet()) {
                set.add(":" + emote + ":");
            }
        }

        if (!set.isEmpty()) {
            player.networkHandler.sendPacket(new ChatSuggestionsS2CPacket(ChatSuggestionsS2CPacket.Action.ADD, new ArrayList<>(set)));
        }
    }

    public static ChatStyle getPersonalStyle(ServerPlayerEntity player) {
        if (player.networkHandler != null) {
            return ((ExtPlayNetworkHandler) player.networkHandler).styledChat$getStyle();
        } else {
            return ChatStyle.EMPTY;
        }
    }

    public static void updateStyle(ServerPlayerEntity player) {
        if (player.networkHandler != null) {
            ((ExtPlayNetworkHandler) player.networkHandler).styledChat$setStyle(createStyleOf(player));
        }
    }

    @Nullable
    public static ChatStyleData getPersonalData(ServerPlayerEntity player) {
        return PlayerDataApi.getCustomDataFor(player, PLAYER_DATA);
    }

    public static ChatStyleData getOrCreatePersonalData(ServerPlayerEntity player) {
        var style = PlayerDataApi.getCustomDataFor(player, PLAYER_DATA);

        if (style == null) {
            style = new VersionedChatStyleData();
            PlayerDataApi.setCustomDataFor(player, PLAYER_DATA, style);
        }
        return style;
    }

    public static void clearPersonalStyleData(ServerPlayerEntity player) {
        PlayerDataApi.setCustomDataFor(player, PLAYER_DATA, new VersionedChatStyleData());
    }

    public static ChatStyle createStyleOf(ServerPlayerEntity player) {
        var style = PlayerDataApi.getCustomDataFor(player, PLAYER_DATA);

        if (style == null) {
            style = new VersionedChatStyleData();
        } else {
            style = (VersionedChatStyleData) style.clone();
        }

        style.fillPermissionOptionProvider(player.getCommandSource());

        return new ChatStyle(style);
    }

    public static MessageType.Parameters createParameters(Text override) {
        return new MessageType.Parameters(StyledChatMod.getMessageType(), override, null);
    }
}
