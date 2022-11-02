package eu.pb4.styledchat.parser;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ClickActionNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static eu.pb4.styledchat.StyledChatUtils.URL_REGEX;

public record LinkParser(TextNode style) implements NodeParser {

    @Override
    public TextNode[] parseNodes(TextNode node) {
        if (node instanceof LiteralNode literalNode) {
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

            return list.toArray(new TextNode[0]);
        } else if (node instanceof ParentTextNode parentTextNode) {
            var list = new ArrayList<TextNode>();

            for (var child : parentTextNode.getChildren()) {
                list.addAll(List.of(this.parseNodes(child)));
            }

            return new TextNode[] { parentTextNode.copyWith(list.toArray(new TextNode[0])) };
        }

        return new TextNode[] { node };
    }

    public static TextNode[] parse(TextNode node, PlaceholderContext context) {
        return new LinkParser(ConfigManager.getConfig().getLinkStyle(context)).parseNodes(node);
    }
}
