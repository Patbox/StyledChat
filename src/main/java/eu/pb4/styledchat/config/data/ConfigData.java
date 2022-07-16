package eu.pb4.styledchat.config.data;

import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.config.ConfigManager;
import net.minecraft.util.Formatting;

import java.util.*;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = ConfigManager.VERSION;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledChat#configuration";
    public ChatStyleData defaultStyle = ChatStyleData.DEFAULT;
    public List<PermissionPriorityStyle> permissionStyles = new ArrayList<>();
    public String petDeathMessage = "${default_message}";

    public Map<String, String> emoticons = getDefaultEmoticons();

    public List<PermissionEmotes> permissionEmoticons = new ArrayList<>();

    public boolean legacyChatFormatting = true;
    public boolean parseLinksInChat = true;
    public boolean enableMarkdown = true;
    public boolean allowModdedDecorators = true;
    public boolean sendFullMessageInChatPreview = false;
    public boolean sendAutoCompletionForTags = false;
    public boolean sendAutoCompletionForTagAliases = false;
    public boolean sendAutoCompletionForEmotes = true;
    public String linkStyle = "<underline><c:#7878ff>${link}";
    public String spoilerStyle = "<gray>${spoiler}";
    public String spoilerSymbol = "▌";
    public HashMap<String, Boolean> defaultEnabledFormatting = getDefaultFormatting();


    private static HashMap<String, Boolean> getDefaultFormatting() {
        HashMap<String, Boolean> map = new HashMap<>();
        for (var tag : TextParserV1.DEFAULT.getTags()) {
            map.put(tag.name(), false);
        }
        map.put(StyledChatUtils.ITEM_TAG, true);
        map.put(StyledChatUtils.POS_TAG, true);
        map.put(StyledChatUtils.SPOILER_TAG, true);
        map.put("bold", true);
        map.put("b", true);
        map.put("italic", true);
        map.put("i", true);
        map.put("strikethrough", true);
        map.put("st", true);
        map.put("underline", true);

        for (var formatting : Formatting.values()) {
            if (formatting.isColor()) {
                map.put(formatting.getName(), true);
            }
        }

        return map;
    }

    private static Map<String, String> getDefaultEmoticons() {
        HashMap<String, String> map = new HashMap<>();

        map.put("shrug", "¯\\_(ツ)_/¯");
        map.put("table", "(╯°□°）╯︵ ┻━┻");
        map.put("heart", "❤");
        map.put("sword", "\uD83D\uDDE1");
        map.put("fire", "\uD83D\uDD25");
        map.put("bow", "\uD83C\uDFF9");
        map.put("trident", "\uD83D\uDD31");
        map.put("rod", "\uD83C\uDFA3");
        map.put("potion", "\uD83E\uDDEA");
        map.put("shears", "✂");
        map.put("bucket", "\uD83E\uDEA3");
        map.put("bell", "\uD83D\uDD14");
        map.put(StyledChatUtils.ITEM_TAG, "[%player:equipment_slot mainhand%]");
        map.put(StyledChatUtils.POS_TAG, "%player:pos_x% %player:pos_y% %player:pos_z%");

        return map;
    }

    public static ConfigData transform(ConfigData configData) {
        for (Map.Entry<String, Boolean> entry : getDefaultFormatting().entrySet()) {
            configData.defaultEnabledFormatting.putIfAbsent(entry.getKey(), entry.getValue());
        }
        configData.defaultEnabledFormatting.putIfAbsent(StyledChatUtils.SPOILER_TAG, true);

        for (var entry : configData.permissionEmoticons) {
            if (entry.emotes != null) {
                entry.emoticons = entry.emotes;
                entry.emotes = null;
            }
        }

        return configData;
    }

    public static class PermissionPriorityStyle {
        public String permission = "";
        public int opLevel = 3;
        public ChatStyleData style = ChatStyleData.DEFAULT;

        public static PermissionPriorityStyle of(String permission, ChatStyleData style) {
            PermissionPriorityStyle priorityStyle = new PermissionPriorityStyle();
            priorityStyle.permission = permission;
            priorityStyle.style = style;
            return priorityStyle;
        }
    }

    public static class PermissionEmotes {
        public String permission = "";
        public int opLevel = 3;
        public Map<String, String> emoticons = Collections.EMPTY_MAP;
        @Deprecated
        public Map<String, String> emotes = null;
    }
}
