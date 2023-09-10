package eu.pb4.styledchat.ducks;

import eu.pb4.styledchat.config.ChatStyle;

public interface ExtPlayNetworkHandler {
    /*@Nullable
    Text styledChat_getLastCached();*/

    void styledChat$setStyle(ChatStyle style);
    ChatStyle styledChat$getStyle();

    boolean styledChat$chatColors();
}
