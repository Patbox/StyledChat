package eu.pb4.styledchat;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;

public class CardboardWarning implements PreLaunchEntrypoint {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean LOADED = FabricLoader.getInstance().isModLoaded("cardboard") || FabricLoader.getInstance().isModLoaded("banner");

    @Override
    public void onPreLaunch() {
        checkAndAnnounce();
    }

    public static void checkAndAnnounce() {
        if (LOADED) {
            LOGGER.error("==============================================");
            for (var i = 0; i < 4; i++) {
                LOGGER.error("");
                LOGGER.error("Cardboard/Banner detected! This mod doesn't work with it!");
                LOGGER.error("You won't get any support as long as it's present!");
                LOGGER.error("");
                LOGGER.error("Read more at: https://gist.github.com/Patbox/e44844294c358b614d347d369b0fc3bf");
                LOGGER.error("");
                LOGGER.error("==============================================");
            }
        }
    }
}
