package eu.pb4.styledchat.command;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("styledchat")
                            .requires(Permissions.require("styledchat.main", true))
                            .executes(Commands::about)

                            .then(literal("reload")
                                    .requires(Permissions.require("styledchat.reload", 3))
                                    .executes(Commands::reloadConfig)
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

                                                        var ctx = context.getSource().getPlayer() != null ? PlaceholderContext.of(context.getSource().getPlayer()) : PlaceholderContext.of(context.getSource().getServer());

                                                        parsed = StyledChatUtils.formatFor(ctx, context.getArgument("message", String.class));

                                                        for (var player : EntityArgumentType.getPlayers(context, "targets")) {
                                                            player.sendMessage(parsed);
                                                        }

                                                        return i;
                                                    }
                                            )
                                    )
                            )
            );
        });
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(Text.literal("Reloaded config!"), false);

            for (var player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                StyledChatUtils.sendAutocompliton(player);
            }
        } else {
            context.getSource().sendError(Text.literal("Error occurred while reloading config! Check console for more information!").formatted(Formatting.RED));

        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Styled Chat")
                .formatted(Formatting.YELLOW)
                .append(Text.literal(" - " + StyledChatMod.VERSION)
                        .formatted(Formatting.WHITE)
                ), false);

        return 1;
    }
}
