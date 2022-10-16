package eu.pb4.styledchat.config.data;

import com.google.gson.annotations.SerializedName;

public class VersionedChatStyleData extends ChatStyleData implements Cloneable {
    @SerializedName("DATA_VERSION")
    public int version = ConfigData.VERSION;
}
