package eu.pb4.styledchat.config.data;

import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = ConfigManager.VERSION;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledChat#configuration";
    public ChatStyleData defaultStyle = ChatStyleData.getDefault();
    public HashMap<String, ChatStyleData> permissionStyles = new HashMap<>();
    public boolean legacyChatFormatting = false;
    public HashMap<String, Boolean> defaultEnabledFormatting = getDefaultFormatting();


    private static HashMap<String, Boolean> getDefaultFormatting() {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String string : TextParser.getRegisteredTags().keySet()) {
            map.put(string, false);
        }
        map.put("item", true);
        return map;
    }

    public static ConfigData transform(ConfigData configData) {
        for (Map.Entry<String, Boolean> entry : getDefaultFormatting().entrySet()) {
            configData.defaultEnabledFormatting.putIfAbsent(entry.getKey(), entry.getValue());
        }
        return configData;
    }
}
