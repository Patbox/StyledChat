package eu.pb4.styledchat.config.data.old;

import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.styledchat.config.data.ConfigData;
import net.minecraft.util.Pair;

import java.util.*;

public class ConfigDataV2 {
    public static final int VERSION = 2;
    public int CONFIG_VERSION_DONT_TOUCH_THIS = VERSION;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledChat#configuration";
    public ChatStyleDataV2 defaultStyle;
    public List<PermissionPriorityStyle> permissionStyles = new ArrayList<>();
    public String petDeathMessage = "${default_message}";

    public Map<String, String> emoticons = new HashMap<>();

    public List<PermissionEmotes> permissionEmoticons = new ArrayList<>();

    public boolean legacyChatFormatting = true;
    public boolean parseLinksInChat = true;
    public boolean enableMarkdown = true;
    public boolean allowModdedDecorators = true;
    public boolean sendFullMessageInChatPreview = false;
    public boolean requireChatPreviewForFormatting = false;
    public boolean sendAutoCompletionForTags = false;
    public boolean sendAutoCompletionForTagAliases = false;
    public boolean sendAutoCompletionForEmotes = true;
    public String linkStyle = "<underline><c:#7878ff>${link}";
    public String spoilerStyle = "<gray>${spoiler}";
    public String spoilerSymbol = "▌";
    public HashMap<String, Boolean> defaultEnabledFormatting = new HashMap<>();

    public ConfigData update() {
        var data = new ConfigData();
        data.defaultStyle = this.defaultStyle.update();
        data.defaultStyle.messages.petDeathMessage = this.petDeathMessage;
        data.defaultStyle.linkStyle = this.linkStyle;
        data.defaultStyle.spoilerStyle = this.spoilerStyle;
        data.defaultStyle.spoilerSymbol = this.spoilerSymbol;
        data.defaultStyle.emoticons.putAll(this.emoticons);

        for (var e : this.defaultEnabledFormatting.entrySet()) {
            if (e.getValue()) {
                data.defaultStyle.formatting.put(e.getKey(), true);
            }
        }
        data.autoCompletion.tagAliases = this.sendAutoCompletionForTagAliases;
        data.autoCompletion.tags = this.sendAutoCompletionForTags;
        data.autoCompletion.emoticons = this.sendAutoCompletionForEmotes;

        data.formatting.markdown = this.enableMarkdown;
        data.formatting.legacyChatFormatting = this.legacyChatFormatting;
        data.formatting.allowModdedDecorators = this.allowModdedDecorators;
        data.formatting.parseLinksInChat = this.parseLinksInChat;

        var pairs = new ArrayList<Pair<PermissionPriorityStyle, PermissionEmotes>>();

        for (var x : this.permissionStyles) {
            pairs.add(new Pair<>(x, null));
        }

        for (var x : this.permissionEmoticons) {
            boolean hasPair = false;
            for (var pair : pairs) {
                if (pair.getRight() == null && pair.getLeft().opLevel == x.opLevel && pair.getLeft().permission.equals(x.permission)) {
                    hasPair = true;
                    pair.setRight(x);
                    break;
                }
            }

            if (!hasPair) {
                pairs.add(new Pair<>(null, x));
            }
        }

        for (var pair : pairs) {
            var style = new ConfigData.RequireChatStyleData();

            if (pair.getLeft() != null) {
                style.require = createRequire(pair.getLeft().permission, pair.getLeft().opLevel);
                pair.getLeft().style.copyInto(style);
            } else {
                style.require = createRequire(pair.getRight().permission, pair.getRight().opLevel);
            }

            if (pair.getRight() != null) {
                style.emoticons.putAll(pair.getRight().emoticons);
            }

            data.permissionStyles.add(style);
        }

        return data;
    }

    private MinecraftPredicate createRequire(String permission, int opLevel) {
        if (permission.isEmpty()) {
            return BuiltinPredicates.operatorLevel(opLevel);
        } else if (opLevel > 4 || opLevel < 1) {
            return BuiltinPredicates.modPermissionApi(permission);
        } else {
            return BuiltinPredicates.modPermissionApi(permission, opLevel);
        }
    }

    public static class PermissionPriorityStyle {
        public ChatStyleDataV2 style;
        public String permission = "";
        public int opLevel = 5;

    }

    public static class PermissionEmotes {
        public String permission = "";
        public int opLevel = 3;
        public Map<String, String> emoticons = Collections.EMPTY_MAP;
    }
}
