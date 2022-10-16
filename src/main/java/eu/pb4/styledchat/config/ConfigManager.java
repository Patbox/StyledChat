package eu.pb4.styledchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.config.data.ConfigData;
import eu.pb4.styledchat.config.data.VersionConfigData;
import eu.pb4.styledchat.config.data.old.ConfigDataV2;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient()
            .registerTypeHierarchyAdapter(MinecraftPredicate.class, GsonPredicateSerializer.INSTANCE).create();

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
            var configFile = FabricLoader.getInstance().getConfigDir().resolve("styled-chat.json");


            if (Files.exists(configFile)) {
                String json = Files.readString(configFile, StandardCharsets.UTF_8);
                VersionConfigData versionConfigData = GSON.fromJson(json, VersionConfigData.class);


                if (versionConfigData.version < 3) {
                    config = GSON.fromJson(json, ConfigDataV2.class).update();
                    Files.writeString(FabricLoader.getInstance().getConfigDir().resolve("styled-chat.json_old_v2"), json, StandardCharsets.UTF_8);
                } else {
                    config = GSON.fromJson(json, ConfigData.class);
                }

                config.defaultStyle.fillMissing();
            } else {
                config = new ConfigData();
            }

            Files.writeString(configFile, GSON.toJson(config), StandardCharsets.UTF_8);

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
