package eu.pb4.styledchat.config.data;

import java.util.HashMap;
public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 1;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledChat#configuration";
    public ChatStyleData defaultStyle = ChatStyleData.getDefault();
    public HashMap<String, ChatStyleData> permissionStyles = new HashMap<>();
}
