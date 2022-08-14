package eu.pb4.styledchat.other;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ServerTranslationUtils {
    public static final boolean IS_PRESENT = FabricLoader.getInstance().isModLoaded("server_translations_api");

    public static Text translate(ServerPlayerEntity player, Text text) {
        if (IS_PRESENT) {
            return LocalizableText.asLocalizedFor(text, (LocalizationTarget) player);
        } else {
            return text;
        }
    }
}
