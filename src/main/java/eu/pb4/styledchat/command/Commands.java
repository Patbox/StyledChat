package eu.pb4.styledchat.command;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.Iterator;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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
                                                        var parsed = TextParser.parse(StyledChatUtils.formatMessage(context.getArgument("message", String.class), TextParser.getRegisteredTags()));

                                                        for (var player : EntityArgumentType.getPlayers(context, "targets")) {
                                                            player.sendSystemMessage(parsed, Util.NIL_UUID);
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
            context.getSource().sendFeedback(new LiteralText("Reloaded config!"), false);
        } else {
            context.getSource().sendError(new LiteralText("Error accrued while reloading config!").formatted(Formatting.RED));

        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(new LiteralText("Styled Chat")
                .formatted(Formatting.YELLOW)
                .append(new LiteralText(" - " + StyledChatMod.VERSION)
                        .formatted(Formatting.WHITE)
                ), false);

        return 1;
    }
}
