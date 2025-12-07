package eu.pb4.styledchat.parser;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.styledchat.config.ChatStyle;
import eu.pb4.styledchat.config.ConfigManager;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class SpoilerNode extends ParentNode {
    public SpoilerNode(TextNode[] children) {
        super(children);
    }

    @Override
    protected Component applyFormatting(MutableComponent out, ParserContext context) {
        var config = ConfigManager.getConfig();
        var ctx = context.get(PlaceholderContext.KEY);
        var obj = config.getSpoilerStyle(ctx).toText(ctx.asParserContext()
                .with(ChatStyle.DYN_KEY, Map.of("spoiler", Component.literal(config.getSpoilerSymbole(ctx).repeat(out.getString().length())))::get));
        return Component.empty().append(obj).setStyle(obj.getStyle().withHoverEvent(new HoverEvent.ShowText(out)));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new SpoilerNode(this.children);
    }
}
