package eu.pb4.styledchat;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.EmptyNode;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.*;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import eu.pb4.placeholders.impl.GeneralUtils;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.styledchat.config.ChatStyle;
import eu.pb4.styledchat.config.Config;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.config.data.ChatStyleData;
import eu.pb4.styledchat.config.data.VersionedChatStyleData;
import eu.pb4.styledchat.ducks.ExtPlayNetworkHandler;
import eu.pb4.styledchat.ducks.ExtPlayerChatMessage;
import eu.pb4.styledchat.parser.LinkParser;
import eu.pb4.styledchat.parser.MentionParser;
import eu.pb4.styledchat.parser.SpoilerNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public final class StyledChatUtils {
    public static final Component IGNORED_TEXT = Component.empty();

    public static final Pattern URL_REGEX = Pattern.compile("(https?:\\/\\/[-a-zA-Z0-9@:%._\\+~#=]+\\.[^ ]+)");

    public static final String ITEM_KEY = "item";
    public static final String POS_KEY = "pos";
    public static final String SPOILER_TAG = "spoiler";
    private static final Function<MutableComponent, MutableComponent> COLOR_CLEARING = (t) -> t.setStyle(t.getStyle().withColor((TextColor) null));

    public static JsonDataStorage<VersionedChatStyleData> PLAYER_DATA = new JsonDataStorage<>("styled_chat_style", VersionedChatStyleData.class);
    public static final TextTag SPOILER_TEXT_TAG_NEW = TextTag.enclosing(SPOILER_TAG, List.of("hide"), "styledchat", true, ((nodes, arg, parser) -> new SpoilerNode(nodes)));

    public static final String FORMAT_PERMISSION_BASE = "styledchat.format.";
    public static final String FORMAT_PERMISSION_UNSAFE = "styledchat.unsafe_format.";


    public static final TagLikeParser.Format EMOTE_FORMAT = TagLikeParser.Format.of(':', ':');
    public static final Component EMPTY_TEXT = Component.empty();
    private static final Set<ResourceKey<ChatType>> DECORABLE = Set.of(ChatType.CHAT, ChatType.EMOTE_COMMAND, ChatType.MSG_COMMAND_INCOMING, ChatType.MSG_COMMAND_OUTGOING, ChatType.SAY_COMMAND, ChatType.TEAM_MSG_COMMAND_INCOMING, ChatType.TEAM_MSG_COMMAND_OUTGOING);

    @Deprecated(forRemoval = true)
    public static final TextParserV1.TagNodeBuilder SPOILER_TAG_HANDLER = (tag, data, input, handlers, endAt) -> {
        var out = TextParserV1.parseNodesWith(input, handlers, endAt);

        return new TextParserV1.TagNodeValue(new SpoilerNode(out.nodes()), out.length());
    };

    @Deprecated(forRemoval = true)
    public static final TextParserV1.TextTag SPOILER_TEXT_TAG = TextParserV1.TextTag.of(SPOILER_TAG, List.of("hide"), "styledchat", true, SPOILER_TAG_HANDLER);
    @Deprecated(forRemoval = true)
    public static final Pattern EMOTE_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))[:](?<id>[^:]+)[:]");;

    @Deprecated
    public static TextNode parseText(String input) {
        return !input.isEmpty() ? Placeholders.parseNodes(TextParserUtils.formatNodes(input)) : EmptyNode.INSTANCE;
    }

    public static NodeParser createParser(CommandSourceStack source) {
        return createParser(PlaceholderContext.of(source));
    }

    public static NodeParser createParser(PlaceholderContext context) {
        var config = ConfigManager.getConfig();
        var builder = NodeParser.builder();

        var tags = getTextTagRegistry(context.source());

        if (!tags.getTags().isEmpty()) {
            builder.simplifiedTextFormat();
            builder.quickText();
            builder.customTagRegistry(tags);
        }
        if (config.configData.formatting.parseLinksInChat) {
            builder.add(new LinkParser(ConfigManager.getConfig().getLinkStyle(context)));
        }

        if (config.configData.formatting.parseMentionsInChat) {
            builder.add(new MentionParser(ConfigManager.getConfig().getMentionStyle(context), context));
        }


        if (config.configData.formatting.markdown) {
            var form = new ArrayList<MarkdownLiteParserV1.MarkdownFormat>();

            if (tags.getTag("bold") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.BOLD);
            }

            if (tags.getTag("italic") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.ITALIC);
            }

            if (tags.getTag("underline") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.UNDERLINE);
            }

            if (tags.getTag("strikethrough") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.STRIKETHROUGH);
            }

            if (tags.getTag(SPOILER_TAG) != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.SPOILER);
            }

            if (tags.getTag("link") != null) {
                form.add(MarkdownLiteParserV1.MarkdownFormat.URL);
            }

            builder.markdown(SpoilerNode::new, MarkdownLiteParserV1::defaultQuoteFormatting, MarkdownLiteParserV1::defaultUrlFormatting,
                    form.toArray(new MarkdownLiteParserV1.MarkdownFormat[0]));
        }

        if (config.configData.formatting.legacyChatFormatting) {
            var form = new ArrayList<ChatFormatting>();
            for (var formatting : ChatFormatting.values()) {
                if (tags.getTag(formatting.getName()) != null) {
                    form.add(formatting);
                }
            }

            boolean color = tags.getTag("color") != null;

            if (!form.isEmpty() || color) {
                builder.legacy(color, form.toArray(new ChatFormatting[0]));
            }
        }

        var emotes = getEmotes(context);

        if (!emotes.isEmpty()) {
            builder.placeholders(EMOTE_FORMAT, emotes::get);
        }

        return builder.build();
    }

    public static TagRegistry getTextTagRegistry(CommandSourceStack source) {
        Config config = ConfigManager.getConfig();
        var registry = TagRegistry.create();

        var allowedFormatting = config.getAllowedFormatting(source);

        for (var entry : TagRegistry.DEFAULT.getTags()) {
            if (allowedFormatting.getBoolean(entry.name())
                    || Permissions.check(source, (entry.userSafe() ? FORMAT_PERMISSION_BASE : FORMAT_PERMISSION_UNSAFE) + entry.name(), entry.userSafe() ? 2 : 4)
                    || Permissions.check(source, (entry.userSafe() ? FORMAT_PERMISSION_BASE : FORMAT_PERMISSION_UNSAFE) + ".type." + entry.type(), entry.userSafe() ? 2 : 4)
            ) {
                registry.register(entry);
            }
        }

        if (allowedFormatting.getBoolean(SPOILER_TAG)
                || Permissions.check(source, FORMAT_PERMISSION_BASE + SPOILER_TAG, 2)) {
            registry.register(SPOILER_TEXT_TAG_NEW);
        }

        return registry;
    }
    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public static TextParserV1 createTextParserV1(CommandSourceStack source) {
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
        return StyledChatStyles.getEmotes(context.hasPlayer() ? context.player().createCommandSourceStack() : context.server().createCommandSourceStack());
    }

    public static Component formatFor(PlaceholderContext context, String input) {
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

        if (config.configData.formatting.respectColors) {
            try {
                text = context.server().getChatDecorator().decorate(context.player(), text);
            } catch (Exception e) {
                // noop
            }
        }

        return text;
    }

    private static String getMarkdownRegex(String base, String sides) {
        return "(" + sides + ")(?<id>[^" + base + "]+)(" + sides + ")";
    }

    public static <T> ChatDecorator getCommandDecorator(String context, CommandSourceStack source, BiFunction<String, Class<?>, Object> argumentGetter) {
        Config config = ConfigManager.getConfig();


        return (player, message) -> {
            var input = formatFor(player != null ? PlaceholderContext.of(player) : PlaceholderContext.of(StyledChatMod.server), message.getString());


            return switch (context) {
                case "msg" -> {
                    try {
                        yield config.getPrivateMessageReceived(
                                source.getDisplayName(),
                                ((EntitySelector) argumentGetter.apply("targets", EntitySelector.class)).findPlayers(source).get(0).getDisplayName(),
                                input, source
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        yield Component.literal("");
                    }
                }
                case "teammsg" -> {
                    try {
                        yield config.getTeamChatReceived(((PlayerTeam) source.getEntity().getTeam()).getFormattedDisplayName(),
                                source.getDisplayName(),
                                input, source
                        );
                    } catch (Exception e) {
                        yield Component.literal("");
                    }
                }

                case "say" -> config.getSayCommand(source, input);

                case "me" -> config.getMeCommand(source, input);

                default -> input;
            };
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

    public static ChatType.Bound removeColor(ChatType.Bound parameters) {
        return new ChatType.Bound(parameters.chatType(), removeColor(parameters.name()), parameters.targetName());
    }

    public static Component removeColor(Component text) {
        // Should expose this as a function is tpapi, but too lazt for now
        return GeneralUtils.cloneTransformText(text, COLOR_CLEARING);
    }

    public static boolean isHandledByMod(ResourceKey<ChatType> typeKey) {
        return DECORABLE.contains(typeKey);
    }

    /*public static void modifyForSending(FilteredMessage<SignedMessage> message, ServerCommandSource source, RegistryKey<MessageType> type) {
        ((ExtPlayerChatMessage) (Object) message.raw()).styledChat_setArg("override", StyledChatUtils.formatMessage(message.raw(), source, type));

        if (message.raw() != message.filtered()) {
            ((ExtPlayerChatMessage) (Object) message.filtered()).styledChat_setArg("override", StyledChatUtils.formatMessage(message.filtered(), source, type));
        }
    }*/

    public static void modifyForSending(PlayerChatMessage message, CommandSourceStack source, ResourceKey<ChatType> type) {
        try {
             ExtPlayerChatMessage.setArg(message, "override", StyledChatUtils.formatMessage(message, source, type));
            ((ExtPlayerChatMessage) (Object) message).styledChat_setType(type);
            ((ExtPlayerChatMessage) (Object) message).styledChat_setSource(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Component formatMessage(PlayerChatMessage message, CommandSourceStack source, ResourceKey<ChatType> type) {
        var ext = (ExtPlayerChatMessage) (Object) message;

        var baseInput = ext.styledChat_getArg("base_input");

        var input = baseInput != null && baseInput.getContents() != PlainTextContents.EMPTY
                ? baseInput
                : maybeFormatFor(source, ext.styledChat_getOriginal(), message.decoratedContent());

        if (baseInput == StyledChatUtils.EMPTY_TEXT) {
            ext.styledChat_setArg("base_input", input);
        }

        return switch (type.identifier().getPath()) {
            case "msg_command_incoming" -> {
                try {
                    yield StyledChatStyles.getPrivateMessageReceived(
                            source.getDisplayName(),
                            ext.styledChat_getArg("targets"),
                            input, source
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    yield Component.empty();
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
                    yield Component.empty();
                }
            }
            case "team_msg_command_incoming" -> {
                try {
                    yield StyledChatStyles.getTeamChatReceived(((PlayerTeam) source.getEntity().getTeam()).getFormattedDisplayName(),
                            source.getDisplayName(),
                            input, source
                    );
                } catch (Exception e) {
                    yield Component.literal("");
                }
            }

            case "team_msg_command_outgoing" -> {
                try {
                    yield StyledChatStyles.getTeamChatSent(((PlayerTeam) source.getEntity().getTeam()).getFormattedDisplayName(),
                            source.getDisplayName(),
                            input, source
                    );
                } catch (Exception e) {
                    yield Component.literal("");
                }
            }
            case "say_command" -> StyledChatStyles.getSayCommand(source, input);

            case "emote_command" -> StyledChatStyles.getMeCommand(source, input);

            case "chat" -> StyledChatStyles.getChat(source.getPlayer(), input);

            default -> StyledChatStyles.getCustom(type.identifier(), source.getDisplayName(), input, null, source);
        };
    }

    public static Component maybeFormatFor(CommandSourceStack source, String original, Component originalContent) {
        return formatFor(source, original);
    }

    public static Component formatFor(CommandSourceStack source, String original) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return formatFor(PlaceholderContext.of(player), original);
        } else {
            return formatFor(PlaceholderContext.of(source.getServer()), original);
        }
    }

    @Deprecated
    public static PlayerChatMessage toEventMessage(PlayerChatMessage message, PlaceholderContext context) {
        var ext = (ExtPlayerChatMessage) (Object) message;

        var baseInput = ext.styledChat_getArg("base_input");
        var input = baseInput != StyledChatUtils.EMPTY_TEXT && baseInput.getContents() != PlainTextContents.EMPTY ? baseInput : formatFor(context, ext.styledChat_getOriginal());
        if (baseInput == StyledChatUtils.EMPTY_TEXT) {
            ext.styledChat_setArg("base_input", input);
        }

        return new PlayerChatMessage(message.link(), null, SignedMessageBody.unsigned(message.signedContent()), input, null);
    }

    @Deprecated(forRemoval = true)
    public static void sendAutocompliton(ServerPlayer player) {
        sendAutoCompletion(player, ConfigManager.getConfig().allPossibleAutoCompletionKeys);
    }

    public static void sendAutoCompletion(ServerPlayer player, Collection<String> oldAutoCompletion) {
        var config = ConfigManager.getConfig();
        player.connection.send(new ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action.REMOVE, new ArrayList<>(oldAutoCompletion)));

        var set = new HashSet<String>();

        var source = player.createCommandSourceStack();

        var handler = StyledChatUtils.getTextTagRegistry(source);

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
            player.connection.send(new ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action.ADD, new ArrayList<>(set)));
        }
    }

    public static ChatStyle getPersonalStyle(ServerPlayer player) {
        if (player.connection != null) {
            return ((ExtPlayNetworkHandler) player.connection).styledChat$getStyle();
        } else {
            return ChatStyle.EMPTY;
        }
    }

    public static void updateStyle(ServerPlayer player) {
        if (player.connection != null) {
            ((ExtPlayNetworkHandler) player.connection).styledChat$setStyle(createStyleOf(player));
        }
    }

    @Nullable
    public static ChatStyleData getPersonalData(ServerPlayer player) {
        return PlayerDataApi.getCustomDataFor(player, PLAYER_DATA);
    }

    public static ChatStyleData getOrCreatePersonalData(ServerPlayer player) {
        var style = PlayerDataApi.getCustomDataFor(player, PLAYER_DATA);

        if (style == null) {
            style = new VersionedChatStyleData();
            PlayerDataApi.setCustomDataFor(player, PLAYER_DATA, style);
        }
        return style;
    }

    public static void clearPersonalStyleData(ServerPlayer player) {
        PlayerDataApi.setCustomDataFor(player, PLAYER_DATA, new VersionedChatStyleData());
    }

    public static ChatStyle createStyleOf(ServerPlayer player) {
        var style = PlayerDataApi.getCustomDataFor(player, PLAYER_DATA);

        if (style == null) {
            style = new VersionedChatStyleData();
        } else {
            style = (VersionedChatStyleData) style.clone();
        }

        style.fillPermissionOptionProvider(player.createCommandSourceStack());

        return new ChatStyle(style);
    }

    public static ChatType.Bound createParameters(Component override) {
        return ChatType.bind(StyledChatMod.MESSAGE_TYPE_ID, StyledChatMod.server.registryAccess(), override);
    }
}
