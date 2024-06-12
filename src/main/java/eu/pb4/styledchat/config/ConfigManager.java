package eu.pb4.styledchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.styledchat.StyledChatMod;
import eu.pb4.styledchat.config.data.ConfigData;
import eu.pb4.styledchat.config.data.VersionConfigData;
import eu.pb4.styledchat.config.data.old.ConfigDataV2;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryWrapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigManager {
    private static Config config = null;
    private static ConfigData configData = null;


    public static Config getConfig() {
        if (config == null) {
            if (configData == null) {
                return Config.DEFAULT;
            }
            config = new Config(configData);
        }

        return config;
    }

    public static void clearCached() {
        config = null;
    }

    public static boolean loadConfig(RegistryWrapper.WrapperLookup lookup) {
        config = null;
        try {
            var gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient()
                    .registerTypeHierarchyAdapter(MinecraftPredicate.class, GsonPredicateSerializer.create(lookup)).create();

            ConfigData config;
            var configFile = FabricLoader.getInstance().getConfigDir().resolve("styled-chat.json");

            if (Files.exists(configFile)) {
                String json = Files.readString(configFile, StandardCharsets.UTF_8);
                VersionConfigData versionConfigData = gson.fromJson(json, VersionConfigData.class);


                if (versionConfigData.version < 3) {
                    config = gson.fromJson(json, ConfigDataV2.class).update();
                    Files.writeString(FabricLoader.getInstance().getConfigDir().resolve("styled-chat.json_old_v2"), json, StandardCharsets.UTF_8);
                } else {
                    config = gson.fromJson(json, ConfigData.class);
                }

                config.defaultStyle.fillMissing();
            } else {
                config = new ConfigData();
            }

            Files.writeString(configFile, gson.toJson(config), StandardCharsets.UTF_8);

            configData = config;
            return true;
        } catch (Exception exception) {
            StyledChatMod.LOGGER.error("Something went wrong while reading config! Make sure format is correct!");
            exception.printStackTrace();
            if (configData == null) {
                configData = new ConfigData();
            }
            return false;
        }
    }

    public static JsonObject loadJson(String key) {
        var path = FabricLoader.getInstance().getConfigDir().resolve(key);
        if (Files.exists(path)) {
            try {
                return JsonParser.parseReader(Files.newBufferedReader(path)).getAsJsonObject();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return new JsonObject();
    }

    public static JsonObject loadJsonBuiltin(String baseValue) {
        var path = StyledChatMod.CONTAINER.findPath("emoji/" + baseValue + ".json");
        if (path.isPresent()) {
            try {
                return JsonParser.parseReader(Files.newBufferedReader(path.get())).getAsJsonObject();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return new JsonObject();

    }
}
