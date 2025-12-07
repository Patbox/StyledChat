package eu.pb4.styledchat.parser;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.DirectTextNode;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.drex.vanish.api.VanishAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record MentionParser(TextNode style, PlaceholderContext context) implements NodeParser {

    public static final boolean VANISH = FabricLoader.getInstance().isModLoaded("melius-vanish");

    @Override
    public TextNode[] parseNodes(TextNode node) {
        if (node instanceof LiteralNode literalNode) {
            return parseInput(literalNode.value());
        } else if (node instanceof ParentTextNode parentTextNode) {
            var list = new ArrayList<TextNode>();

            for (var child : parentTextNode.getChildren()) {
                list.addAll(List.of(this.parseNodes(child)));
            }

            return new TextNode[]{parentTextNode.copyWith(list.toArray(new TextNode[]{}))};
        }

        return new TextNode[]{node};
    }

    public TextNode[] parseInput(String input) {
        if (input.isEmpty()) return new TextNode[]{};
        for (ServerPlayer player : context.server().getPlayerList().getPlayers()) {
            if (VANISH && VanishAPI.isVanished(player)) continue;
            int startPos = input.indexOf(player.getScoreboardName());
            if (startPos != -1) {
                int endPos = startPos + player.getScoreboardName().length();
                TextNode[] before = parseInput(input.substring(0, startPos));
                TextNode mention = new DirectTextNode(style.toText(PlaceholderContext.of(player)));
                TextNode[] after = parseInput(input.substring(Math.min(endPos, input.length())));
                return Stream.of(before, new TextNode[]{mention}, after).flatMap(Stream::of).toArray(TextNode[]::new);
            }
        }
        return new TextNode[]{new LiteralNode(input)};
    }
}
