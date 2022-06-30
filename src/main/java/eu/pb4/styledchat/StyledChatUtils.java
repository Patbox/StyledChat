package eu.pb4.styledchat;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ClickActionNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.styledchat.config.Config;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import eu.pb4.styledchat.parser.SpoilerNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.EntitySelector;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class StyledChatUtils {
    public static final Text IGNORED_TEXT = Text.empty();

    public static final Pattern URL_REGEX = Pattern.compile("(https?:\\/\\/[-a-zA-Z0-9@:%._\\+~#=]+\\.[^ ]+)");

    public static final String ITEM_TAG = "item";
    public static final String POS_TAG = "pos";
    public static final String SPOILER_TAG = "spoiler";


    public static final TextParserV1.TagNodeBuilder SPOILER_TAG_HANDLER = (tag, data, input, handlers, endAt) -> {
        var out = TextParserV1.parseNodesWith(input, handlers, endAt);

        return new TextParserV1.TagNodeValue(new SpoilerNode(out.nodes()), out.length());
    };

    public static final TextParserV1.TextTag SPOILER_TEXT_TAG = TextParserV1.TextTag.of(SPOILER_TAG, List.of("hide"), "styledchat", true, SPOILER_TAG_HANDLER);


    public static final String FORMAT_PERMISSION_BASE = "styledchat.format.";
    public static final String FORMAT_PERMISSION_UNSAFE = "styledchat.unsafe_format.";
    public static final Pattern EMOTE_PATTERN = Pattern.compile("[:](?<id>[^:]+)[:]");
    public static final Text EMPTY_TEXT = Text.empty();
    private static final Set<RegistryKey<MessageType>> DECORABLE = Set.of(MessageType.CHAT, MessageType.EMOTE_COMMAND, MessageType.MSG_COMMAND, MessageType.SAY_COMMAND, MessageType.TEAM_MSG_COMMAND);

    public static TextNode parseText(String input) {
        return !input.isEmpty() ? Placeholders.parseNodes(TextParserUtils.formatNodes(input)) : null;
    }

    public static TextParserV1 createParser(ServerCommandSource source) {
        var parser = new TextParserV1();
        Config config = ConfigManager.getConfig();

        for (var entry : TextParserV1.DEFAULT.getTags()) {
            if (config.defaultFormattingCodes.getBoolean(entry.name())
                    || Permissions.check(source, (entry.userSafe() ? FORMAT_PERMISSION_BASE : FORMAT_PERMISSION_UNSAFE) + entry.name(), entry.userSafe() ? 2 : 4)
                    || Permissions.check(source, (entry.userSafe() ? FORMAT_PERMISSION_BASE : FORMAT_PERMISSION_UNSAFE) + ".type." + entry.type(), entry.userSafe() ? 2 : 4)
            ) {
                parser.register(entry);
            }
        }

        if (config.defaultFormattingCodes.getBoolean(SPOILER_TAG)
                || Permissions.check(source, FORMAT_PERMISSION_BASE + SPOILER_TAG, 2)) {
            parser.register(SPOILER_TEXT_TAG);
        }

        StyledChatEvents.FORMATTING_CREATION_EVENT.invoker().onFormattingBuild(source, parser);

        return parser;
    }

    public static Map<String, TextNode> getEmotes(PlaceholderContext context) {
        return ConfigManager.getConfig().getEmotes(context.hasPlayer() ? context.player().getCommandSource() : context.server().getCommandSource());
    }

    public static Text formatFor(PlaceholderContext context, String input) {
        var parser = createParser(context.hasPlayer() ? context.player().getCommandSource() : context.server().getCommandSource());
        var config = ConfigManager.getConfig();
        if (config.configData.enableMarkdown || config.configData.legacyChatFormatting) {
            input = legacyFormatMessage(input, parser.getTags().stream().map((x) -> x.name()).collect(Collectors.toSet()));
        }
        input = StyledChatEvents.PRE_MESSAGE_CONTENT.invoker().onPreMessage(input, context);


        var emotes = getEmotes(context);
        var text = Placeholders.parseText(
                StyledChatEvents.MESSAGE_CONTENT.invoker().onMessage(additionalParsing(new ParentNode(parser.parseNodes(new LiteralNode(input)))), context),
                context,
                EMOTE_PATTERN,
                (id) -> emotes.containsKey(id) ? ((ctx, arg) -> PlaceholderResult.value(Placeholders.parseText(emotes.get(id), ctx))) : null
        );

        if (config.configData.allowModdedDecorators) {
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

        if (config.configData.legacyChatFormatting) {
            for (var formatting : Formatting.values()) {
                if (handlers.contains(formatting.getName())) {
                    input = input.replace("&" + formatting.getCode(), "<" + formatting.getName() + ">");
                }
            }
        }

        try {
            if (config.configData.enableMarkdown) {
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
            Config config = ConfigManager.getConfig();

            if (player != null) {
                return CompletableFuture.completedFuture(config.getChat(player, formatFor(PlaceholderContext.of(player), message.getString())));
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
                        yield config.getPrivateMessageSent(
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
                        yield config.getTeamChatSent(((Team) source.getEntity().getScoreboardTeam()).getFormattedName(),
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
    public static TextNode additionalParsing(TextNode node) {
        var config = ConfigManager.getConfig();
        if (config.configData.parseLinksInChat) {
            node = parseLinks(node);
        }
        return node;
    }

    public static TextNode parseLinks(TextNode node) {
        if (node instanceof LiteralNode literalNode) {
            var style = ConfigManager.getConfig().linkStyle;
            var input = literalNode.value();
            var list = new ArrayList<TextNode>();

            Matcher matcher = URL_REGEX.matcher(input);
            int currentPos = 0;
            int currentEnd = input.length();

            while (matcher.find()) {
                if (currentEnd <= matcher.start()) {
                    break;
                }

                String betweenText = input.substring(currentPos, matcher.start());

                if (betweenText.length() != 0) {
                    list.add(new LiteralNode(betweenText));
                }

                list.add(new ClickActionNode(Placeholders.parseNodes(style, Placeholders.PREDEFINED_PLACEHOLDER_PATTERN, Map.of("link", Text.literal(matcher.group()))).getChildren(), ClickEvent.Action.OPEN_URL, new LiteralNode(matcher.group())));

                currentPos = matcher.end();
            }

            if (currentPos < currentEnd) {
                String restOfText = input.substring(currentPos, currentEnd);
                if (restOfText.length() != 0) {
                    list.add(new LiteralNode(restOfText));
                }
            }

            return list.size() == 1 ? list.get(0) : new ParentNode(list.toArray(new TextNode[0]));
        } else if (node instanceof ParentTextNode parentTextNode) {
            var list = new ArrayList<TextNode>();

            for (var child : parentTextNode.getChildren()) {
                list.add(parseLinks(child));
            }

            return parentTextNode.copyWith(list.toArray(new TextNode[0]));
        }

        return node;
    }

    public static boolean isHandledByMod(RegistryKey<MessageType> typeKey) {
        return DECORABLE.contains(typeKey);
    }

    public static void modifyForSending(FilteredMessage<SignedMessage> message, ServerCommandSource source, RegistryKey<MessageType> type) {
        ((ExtSignedMessage) (Object) message.raw()).styledChat_setArg("override", StyledChatUtils.formatMessage(message.raw(), source, type));

        if (message.raw() != message.filtered()) {
            ((ExtSignedMessage) (Object) message.raw()).styledChat_setArg("override", StyledChatUtils.formatMessage(message.filtered(), source, type));
        }
    }

    public static void modifyForSending(SignedMessage message, ServerCommandSource source, RegistryKey<MessageType> type) {
        ((ExtSignedMessage) (Object) message).styledChat_setArg("override", StyledChatUtils.formatMessage(message, source, type));
    }

    public static Text formatMessage(SignedMessage message, ServerCommandSource source, RegistryKey<MessageType> type) {
        Config config = ConfigManager.getConfig();
        var ext = (ExtSignedMessage) (Object) message;

        var baseInput = ext.styledChat_getArg("base_input");
        var input = baseInput != null && baseInput.getContent() != TextContent.EMPTY ? baseInput : formatFor(source, ext.styledChat_getOriginal());

        return switch (type.getValue().getPath()) {
            case "msg_command" -> {
                try {
                    yield config.getPrivateMessageReceived(
                            source.getDisplayName(),
                            ext.styledChat_getArg("targets"),
                            input, source
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    yield Text.empty();
                }
            }
            case "team_msg_command" -> {
                try {
                    yield config.getTeamChatReceived(((Team) source.getEntity().getScoreboardTeam()).getFormattedName(),
                            source.getDisplayName(),
                            input, source
                    );
                } catch (Exception e) {
                    yield Text.literal("");
                }
            }

            case "say_command" -> config.getSayCommand(source, input);

            case "emote_command" -> config.getMeCommand(source, input);

            case "chat" -> config.getChat(source.getPlayer(), input);

            default -> input;
        };
    }

    public static Text formatFor(ServerCommandSource source, String original) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            return formatFor(PlaceholderContext.of(player), original);
        } else {
            return formatFor(PlaceholderContext.of(source.getServer()), original);
        }
    }

    public static FilteredMessage<SignedMessage> toEventMessage(FilteredMessage<SignedMessage> message, PlaceholderContext context) {
        var ext = (ExtSignedMessage) (Object) message.raw();

        var baseInput = ext.styledChat_getArg("base_input");
        var input = baseInput != null && baseInput.getContent() != TextContent.EMPTY ? baseInput : formatFor(context, ext.styledChat_getOriginal());

        return FilteredMessage.permitted(SignedMessage.of(input));
    }
}
