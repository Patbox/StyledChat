package eu.pb4.styledchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.config.data.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class ConfigManager {
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
                config = GSON.fromJson(new InputStreamReader(new FileInputStream(configFile), "UTF-8"), ConfigData.class);
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
