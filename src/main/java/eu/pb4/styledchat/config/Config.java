package eu.pb4.styledchat.config;


import com.google.common.collect.ImmutableMap;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledchat.config.data.ChatStyleData;
import eu.pb4.styledchat.config.data.ConfigData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Config {
    //private final ConfigData configData;
    private final ChatStyle defaultStyle;
    private final List<PermissionStyle> permissionStyle;

    public Config(ConfigData data) {
        //this.configData = data;
        this.defaultStyle = new ChatStyle(data.defaultStyle, new ChatStyle(ChatStyleData.getDefault()));

        this.permissionStyle = new ArrayList<>();

        for (Map.Entry<String, ChatStyleData> entry : data.permissionStyles.entrySet()) {
            try {
                this.permissionStyle.add(new PermissionStyle(entry.getKey(), MathHelper.clamp(Integer.parseInt(entry.getKey()), 1, 4), new ChatStyle(entry.getValue())));
            } catch (Exception e) {
                this.permissionStyle.add(new PermissionStyle(entry.getKey(), 4, new ChatStyle(entry.getValue())));
            }
        }

    }

    public Text getDisplayName(ServerPlayerEntity player, Text vanillaDisplayName) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getDisplayName(player, vanillaDisplayName);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getDisplayName(player, vanillaDisplayName);
    }

    public Text getChat(ServerPlayerEntity player, Text message) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getChat(player, message);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getChat(player, message);
    }

    public Text getJoin(ServerPlayerEntity player) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getJoin(player);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getJoin(player);
    }

    public Text getJoinRenamed(ServerPlayerEntity player, String oldName) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getJoinRenamed(player, oldName);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getJoinRenamed(player, oldName);
    }

    public Text getLeft(ServerPlayerEntity player) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getLeft(player);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getLeft(player);
    }

    public Text getDeath(ServerPlayerEntity player, Text vanillaMessage) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getDeath(player, vanillaMessage);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getDeath(player, vanillaMessage);
    }

    public Text getAdvancementTask(ServerPlayerEntity player, Text advancement) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getAdvancementTask(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementTask(player, advancement);
    }

    public Text getAdvancementGoal(ServerPlayerEntity player, Text advancement) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getAdvancementGoal(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementGoal(player, advancement);
    }

    public Text getAdvancementChallenge(ServerPlayerEntity player, Text advancement) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionStyle entry : this.permissionStyle) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = entry.style.getAdvancementChallenge(player, advancement);
                if (text != null) {
                    return text;
                }
            }
        }
        return this.defaultStyle.getAdvancementChallenge(player, advancement);
    }

    private static record PermissionStyle(String permission, int opLevel, ChatStyle style) {
    }
}
