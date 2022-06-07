package eu.pb4.styledchat.ducks;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface ExtPlayNetworkHandler {
    @Nullable
    Text styledChat_getLastCached();
}
