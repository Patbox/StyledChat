package eu.pb4.styledchat.ducks;

import net.minecraft.server.command.ServerCommandSource;

import java.util.function.BiFunction;

public interface ExtMessageFormat {
    <T> void styledChat_setSource(String command, ServerCommandSource source, BiFunction<String, Class<T>, T> argumentGetter);
}
