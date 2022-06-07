package eu.pb4.styledchat.parser;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

public class SpoilerNode extends ParentNode {
    public SpoilerNode(TextNode[] children) {
        super(children);
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        var config = ConfigManager.getConfig();
        var obj = ((MutableText) Placeholders.parseText(config.spoilerStyle,
                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                Map.of("spoiler", Text.literal(config.configData.spoilerSymbol.repeat(out.getString().length())))
        ));

        return obj.setStyle(obj.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, out)));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new SpoilerNode(this.children);
    }
}
