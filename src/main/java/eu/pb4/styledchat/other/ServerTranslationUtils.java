package eu.pb4.styledchat.other;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ServerTranslationUtils {
    public static final boolean IS_PRESENT = FabricLoader.getInstance().isModLoaded("server_translations_api");
    public static final boolean IS_BROKEN;

    static {
        boolean IS_BROKEN1;
        try {
            IS_BROKEN1 = IS_PRESENT && FabricLoader.getInstance().getModContainer("server_translations_api")
                    .get().getMetadata().getVersion().compareTo(Version.parse("1.4.17")) < 0;
        } catch (VersionParsingException e) {
            IS_BROKEN1 = false;
            e.printStackTrace();
        }
        IS_BROKEN = IS_BROKEN1;
    }

    public static Text translateIfBreaks(ServerPlayerEntity player, Text text) {
        if (IS_BROKEN) {
            return translate(player, text);
        }
        return text;
    }
    public static Text translate(ServerPlayerEntity player, Text text) {
        if (IS_PRESENT) {
            return LocalizableText.asLocalizedFor(text, (LocalizationTarget) player);
        } else {
            return text;
        }
    }
}
