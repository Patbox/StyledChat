package eu.pb4.styledchat.config.data;

import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.config.ConfigManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = ConfigManager.VERSION;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledChat#configuration";
    public ChatStyleData defaultStyle = ChatStyleData.DEFAULT;
    public List<PermissionPriorityStyle> permissionStyles = new ArrayList<>();
    public boolean legacyChatFormatting = false;
    public boolean parseLinksInChat = true;
    public String linkStyle = "<underline><blue>${link}";
    public HashMap<String, Boolean> defaultEnabledFormatting = getDefaultFormatting();


    private static HashMap<String, Boolean> getDefaultFormatting() {
        HashMap<String, Boolean> map = new HashMap<>();
        for (String string : TextParser.getRegisteredTags().keySet()) {
            if (string.equals("click")) {
                continue;
            }
            map.put(string, false);
        }
        map.put("item", true);
        map.put("pos", true);
        return map;
    }

    public static ConfigData transform(ConfigData configData) {
        for (Map.Entry<String, Boolean> entry : getDefaultFormatting().entrySet()) {
            configData.defaultEnabledFormatting.putIfAbsent(entry.getKey(), entry.getValue());
        }
        return configData;
    }

    public static class PermissionPriorityStyle {
        public String permission = "";
        public ChatStyleData style = ChatStyleData.DEFAULT;

        public static PermissionPriorityStyle of(String permission, ChatStyleData style) {
            PermissionPriorityStyle priorityStyle = new PermissionPriorityStyle();
            priorityStyle.permission = permission;
            priorityStyle.style = style;
            return priorityStyle;
        }
    }
}
