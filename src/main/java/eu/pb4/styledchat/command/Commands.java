package eu.pb4.styledchat.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import eu.pb4.styledchat.config.data.ChatStyleData;
import eu.pb4.styledchat.other.GenericModInfo;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("styledchat")
                        .requires(Permissions.require("styledchat.main", true))
                        .executes(Commands::about)

                        .then(literal("reload")
                                .requires(Permissions.require("styledchat.reload", 3))
                                .executes(Commands::reloadConfig)
                        )

                        .then(literal("set")
                                .requires(Permissions.require("styledchat.set", 2))
                                .then(fillWithProperties(argument("players", EntityArgumentType.players()),
                                        (x, p) -> x.then(argument("value", StringArgumentType.greedyString())
                                                .executes((ctx) -> Commands.setProperty(ctx, p.apply(ctx)))
                                        ))
                                )
                        )

                        .then(literal("get")
                                .requires(Permissions.require("styledchat.get", 2))
                                .then(fillWithProperties(argument("player", EntityArgumentType.player()),
                                        (x, p) -> x.executes((ctx) -> Commands.getProperty(ctx, p.apply(ctx)))
                                ))
                        )

                        .then(literal("clear")
                                .requires(Permissions.require("styledchat.clear", 3))
                                .then(fillWithProperties(argument("players", EntityArgumentType.players()),
                                        (x, p) -> x.executes((ctx) -> Commands.clearProperty(ctx, p.apply(ctx)))
                                ).then(literal("*").executes((ctx) -> Commands.clearProperty(ctx, null))))
                        )
        );

        dispatcher.register(
                literal("tellform")
                        .requires(Permissions.require("styledchat.tellform", 2))

                        .then(argument("targets", EntityArgumentType.players())
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes((context) -> {
                                                    int i = 0;
                                                    Text parsed;


                                                    parsed = Placeholders.parseText(
                                                            TextNode.asSingle(
                                                                    StyledChatUtils.createParser(context.getSource()).parseNodes(TextNode.of(StringArgumentType.getString(context, "message")))
                                                            ),
                                                            PlaceholderContext.of(context.getSource())
                                                    );

                                                    for (var player : EntityArgumentType.getPlayers(context, "targets")) {
                                                        player.sendMessage(parsed);
                                                    }

                                                    return i;
                                                }
                                        )
                                )
                        )
        );

    }

    private static int getProperty(CommandContext<ServerCommandSource> context, ChatStyleData.PropertyGetSet propertyGetSet) throws CommandSyntaxException {
        var player = EntityArgumentType.getPlayer(context, "player");

        var data = StyledChatUtils.getPersonalData(player);

        if (data == null) {
            context.getSource().sendFeedback(() -> Text.literal("<not set>").formatted(Formatting.ITALIC), false);
            return 0;
        } else {
            var val = propertyGetSet.get(data);

            if (val == null) {
                context.getSource().sendFeedback(() -> Text.literal("<not set>").formatted(Formatting.ITALIC), false);
                return 0;
            }

            context.getSource().sendFeedback(() -> Text.literal(val), false);
            return 1;
        }
    }

    private static int setProperty(CommandContext<ServerCommandSource> context, ChatStyleData.PropertyGetSet propertySet) throws CommandSyntaxException {
        var players = EntityArgumentType.getPlayers(context, "players");
        var val = StringArgumentType.getString(context, "value");
        for (var player : players) {
            propertySet.set(StyledChatUtils.getOrCreatePersonalData(player), val);
            StyledChatUtils.updateStyle(player);
        }
        context.getSource().sendFeedback(() -> Text.literal("Changed style of " + players.size() + " player(s)"), false);
        return players.size();
    }

    private static int clearProperty(CommandContext<ServerCommandSource> context, ChatStyleData.PropertyGetSet propertySet) throws CommandSyntaxException {
        var players = EntityArgumentType.getPlayers(context, "players");
        for (var player : players) {
            if (propertySet != null) {
                propertySet.set(StyledChatUtils.getOrCreatePersonalData(player), null);
            } else {
                StyledChatUtils.clearPersonalStyleData(player);
            }
            StyledChatUtils.updateStyle(player);
        }
        context.getSource().sendFeedback(() -> Text.literal("Cleared style for " + players.size() + " player(s)"), false);
        return players.size();
    }

    private static ArgumentBuilder<ServerCommandSource, ?> fillWithProperties(ArgumentBuilder<ServerCommandSource, ?> base, BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, Function<CommandContext<ServerCommandSource>, ChatStyleData.PropertyGetSet>> command) {
        for (var prop : ChatStyleData.PROPERTIES.entrySet()) {
            var x = literal(prop.getKey());
            command.accept(x, (ctx) -> prop.getValue());
            base = base.then(x);
        }

        {
            var x = argument("id", IdentifierArgumentType.identifier())
                    .suggests((context, builder) -> {
                        for (var id : context.getSource().getServer().getRegistryManager().get(RegistryKeys.MESSAGE_TYPE).getIds()) {
                            if (!id.getNamespace().equals("minecraft") && !id.equals(StyledChatMod.MESSAGE_TYPE_ID.getValue())) {
                                builder.suggest(id.toString());
                            }
                        }

                        return builder.buildFuture();
                    });

            command.accept(x, (ctx) -> ChatStyleData.PropertyGetSet.ofCustom(IdentifierArgumentType.getIdentifier(ctx, "id").toString()));
            base = base.then(literal("custom").then(x));
        }

        return base;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        var old = ConfigManager.getConfig().allPossibleAutoCompletionKeys;
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(() -> Text.literal("Reloaded config!"), false);

            for (var player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                StyledChatUtils.sendAutoCompletion(player, old);
            }
        } else {
            context.getSource().sendError(Text.literal("Error occurred while reloading config! Check console for more information!").formatted(Formatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        for (var text : context.getSource().getEntity() instanceof ServerPlayerEntity ? GenericModInfo.getAboutFull() : GenericModInfo.getAboutConsole()) {
            context.getSource().sendFeedback(() -> text, false);
        }


        return 1;
    }
}
