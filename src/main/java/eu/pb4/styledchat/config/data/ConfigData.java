package eu.pb4.styledchat.config.data;

import com.google.gson.annotations.SerializedName;
import eu.pb4.predicate.api.MinecraftPredicate;

import java.util.ArrayList;
import java.util.List;

public class ConfigData {
    public static final int VERSION = 3;
    @SerializedName("CONFIG_VERSION_DONT_TOUCH_THIS")
    public int configVersion = VERSION;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledChat#configuration";

    @SerializedName("text_formatting")
    public Formatting formatting = new Formatting();

    public static class Formatting {
        @SerializedName("legacy_formatting")
        public boolean legacyChatFormatting = false;
        @SerializedName("parse_links")
        public boolean parseLinksInChat = true;
        @SerializedName("markdown")
        public boolean markdown = true;
        @SerializedName("formatting_from_other_mods")
        public boolean allowModdedDecorators = false;
    }


    @SerializedName("chat_preview")
    public ChatPreview chatPreview = new ChatPreview();

    public static class ChatPreview {
        @SerializedName("send_full_message")
        public boolean sendFullMessage = false;
        @SerializedName("require_for_formatting")
        public boolean requireForFormatting = false;
    }

    @SerializedName("auto_completion")
    public AutoCompletion autoCompletion = new AutoCompletion();

    public static class AutoCompletion {
        @SerializedName("tags")
        public boolean tags = false;
        @SerializedName("tag_aliases")
        public boolean tagAliases = false;
        @SerializedName("emoticons")
        public boolean emoticons = false;
    }

    @SerializedName("default")
    public ChatStyleData defaultStyle = ChatStyleData.createDefault();

    @SerializedName("styles")
    public List<RequireChatStyleData> permissionStyles = new ArrayList<>();

    public static class RequireChatStyleData extends ChatStyleData {
        @SerializedName("require")
        public MinecraftPredicate require;
    }
}
