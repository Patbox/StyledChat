package eu.pb4.styledchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.config.data.ConfigData;
import eu.pb4.styledchat.config.data.VersionConfigData;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {
    public static final int VERSION = 2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();

    private static Config config = null;
    private static ConfigData configData = null;

    public static Config getConfig() {
        if (config == null) {
            if (configData == null) {
                loadConfig();
            }

            config = new Config(configData);
        }

        return config;
    }

    public static void clearCached() {
        config = null;
    }

    public static boolean loadConfig() {
        config = null;
        try {
            ConfigData config;
            File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "styled-chat.json");


            if (configFile.exists()) {
                String json = IOUtils.toString(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
                VersionConfigData versionConfigData = GSON.fromJson(json, VersionConfigData.class);

                config = ConfigData.transform(switch (versionConfigData.CONFIG_VERSION_DONT_TOUCH_THIS) {
                    default -> GSON.fromJson(json, ConfigData.class);
                });

                config.defaultStyle.fillMissing();
            } else {
                config = new ConfigData();
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            writer.write(GSON.toJson(config));
            writer.close();

            configData = config;
            return true;
        } catch(Exception exception) {
            StyledChatMod.LOGGER.error("Something went wrong while reading config! Make sure format is correct!");
            exception.printStackTrace();
            if (configData == null) {
                configData = new ConfigData();
            }
            return false;
        }
    }
}
