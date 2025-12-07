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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class Commands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, net.minecraft.commands.Commands.CommandSelection environment) {
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
                                .then(fillWithProperties(argument("players", EntityArgument.players()),
                                        (x, p) -> x.then(argument("value", StringArgumentType.greedyString())
                                                .executes((ctx) -> Commands.setProperty(ctx, p.apply(ctx)))
                                        ))
                                )
                        )

                        .then(literal("get")
                                .requires(Permissions.require("styledchat.get", 2))
                                .then(fillWithProperties(argument("player", EntityArgument.player()),
                                        (x, p) -> x.executes((ctx) -> Commands.getProperty(ctx, p.apply(ctx)))
                                ))
                        )

                        .then(literal("clear")
                                .requires(Permissions.require("styledchat.clear", 3))
                                .then(fillWithProperties(argument("players", EntityArgument.players()),
                                        (x, p) -> x.executes((ctx) -> Commands.clearProperty(ctx, p.apply(ctx)))
                                ).then(literal("*").executes((ctx) -> Commands.clearProperty(ctx, null))))
                        )
        );

        dispatcher.register(
                literal("tellform")
                        .requires(Permissions.require("styledchat.tellform", 2))

                        .then(argument("targets", EntityArgument.players())
                                .then(argument("message", StringArgumentType.greedyString())
                                        .executes((context) -> {
                                                    int i = 0;
                                                    Component parsed;


                                                    parsed = Placeholders.parseText(
                                                            TextNode.asSingle(
                                                                    StyledChatUtils.createParser(context.getSource()).parseNodes(TextNode.of(StringArgumentType.getString(context, "message")))
                                                            ),
                                                            PlaceholderContext.of(context.getSource())
                                                    );

                                                    for (var player : EntityArgument.getPlayers(context, "targets")) {
                                                        player.sendSystemMessage(parsed);
                                                    }

                                                    return i;
                                                }
                                        )
                                )
                        )
        );

    }

    private static int getProperty(CommandContext<CommandSourceStack> context, ChatStyleData.PropertyGetSet propertyGetSet) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, "player");

        var data = StyledChatUtils.getPersonalData(player);

        if (data == null) {
            context.getSource().sendSuccess(() -> Component.literal("<not set>").withStyle(ChatFormatting.ITALIC), false);
            return 0;
        } else {
            var val = propertyGetSet.get(data);

            if (val == null) {
                context.getSource().sendSuccess(() -> Component.literal("<not set>").withStyle(ChatFormatting.ITALIC), false);
                return 0;
            }

            context.getSource().sendSuccess(() -> Component.literal(val), false);
            return 1;
        }
    }

    private static int setProperty(CommandContext<CommandSourceStack> context, ChatStyleData.PropertyGetSet propertySet) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "players");
        var val = StringArgumentType.getString(context, "value");
        for (var player : players) {
            propertySet.set(StyledChatUtils.getOrCreatePersonalData(player), val);
            StyledChatUtils.updateStyle(player);
        }
        context.getSource().sendSuccess(() -> Component.literal("Changed style of " + players.size() + " player(s)"), false);
        return players.size();
    }

    private static int clearProperty(CommandContext<CommandSourceStack> context, ChatStyleData.PropertyGetSet propertySet) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "players");
        for (var player : players) {
            if (propertySet != null) {
                propertySet.set(StyledChatUtils.getOrCreatePersonalData(player), null);
            } else {
                StyledChatUtils.clearPersonalStyleData(player);
            }
            StyledChatUtils.updateStyle(player);
        }
        context.getSource().sendSuccess(() -> Component.literal("Cleared style for " + players.size() + " player(s)"), false);
        return players.size();
    }

    private static ArgumentBuilder<CommandSourceStack, ?> fillWithProperties(ArgumentBuilder<CommandSourceStack, ?> base, BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, Function<CommandContext<CommandSourceStack>, ChatStyleData.PropertyGetSet>> command) {
        for (var prop : ChatStyleData.PROPERTIES.entrySet()) {
            var x = literal(prop.getKey());
            command.accept(x, (ctx) -> prop.getValue());
            base = base.then(x);
        }

        {
            var x = argument("id", IdentifierArgument.id())
                    .suggests((context, builder) -> {
                        for (var id : context.getSource().getServer().registryAccess().lookupOrThrow(Registries.CHAT_TYPE).keySet()) {
                            if (!id.getNamespace().equals("minecraft") && !id.equals(StyledChatMod.MESSAGE_TYPE_ID.identifier())) {
                                builder.suggest(id.toString());
                            }
                        }

                        return builder.buildFuture();
                    });

            command.accept(x, (ctx) -> ChatStyleData.PropertyGetSet.ofCustom(IdentifierArgument.getId(ctx, "id").toString()));
            base = base.then(literal("custom").then(x));
        }

        return base;
    }

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        var old = ConfigManager.getConfig().allPossibleAutoCompletionKeys;
        if (ConfigManager.loadConfig(context.getSource().registryAccess())) {
            context.getSource().sendSuccess(() -> Component.literal("Reloaded config!"), false);

            for (var player : context.getSource().getServer().getPlayerList().getPlayers()) {
                StyledChatUtils.sendAutoCompletion(player, old);
            }
        } else {
            context.getSource().sendFailure(Component.literal("Error occurred while reloading config! Check console for more information!").withStyle(ChatFormatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<CommandSourceStack> context) {
        for (var text : context.getSource().getEntity() instanceof ServerPlayer ? GenericModInfo.getAboutFull() : GenericModInfo.getAboutConsole()) {
            context.getSource().sendSuccess(() -> text, false);
        }


        return 1;
    }
}
