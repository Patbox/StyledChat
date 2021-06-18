package eu.pb4.styledchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.config.data.ConfigData;
import eu.pb4.styledchat.config.data.VersionConfigData;
import eu.pb4.styledchat.config.data.old.ConfigDataV1;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class ConfigManager {
    public static final int VERSION = 2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static Config CONFIG;
    private static boolean ENABLED = false;

    public static Config getConfig() {
        return CONFIG;
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static boolean loadConfig() {
        ENABLED = false;

        CONFIG = null;
        try {
            ConfigData config;
            File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "styled-chat.json");


            if (configFile.exists()) {
                String json = IOUtils.toString(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
                VersionConfigData versionConfigData = GSON.fromJson(json, VersionConfigData.class);

                config = ConfigData.transform(switch (versionConfigData.CONFIG_VERSION_DONT_TOUCH_THIS) {
                    case 1 -> GSON.fromJson(json, ConfigDataV1.class).updateToV2();
                    default -> GSON.fromJson(json, ConfigData.class);
                });
            } else {
                config = new ConfigData();
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
            writer.write(GSON.toJson(config));
            writer.close();


            CONFIG = new Config(config);
            ENABLED = true;
        }
        catch(IOException exception) {
            ENABLED = false;
            StyledChatMod.LOGGER.error("Something went wrong while reading config!");
            exception.printStackTrace();
        }

        return ENABLED;
    }
}
