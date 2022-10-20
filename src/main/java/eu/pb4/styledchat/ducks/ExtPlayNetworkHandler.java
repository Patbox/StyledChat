package eu.pb4.styledchat.ducks;

import eu.pb4.styledchat.config.ChatStyle;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface ExtPlayNetworkHandler {
    /*@Nullable
    Text styledChat_getLastCached();*/

    void styledChat$setStyle(ChatStyle style);
    ChatStyle styledChat$getStyle();
}
