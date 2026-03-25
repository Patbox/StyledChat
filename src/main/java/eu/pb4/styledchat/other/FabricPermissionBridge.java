package eu.pb4.styledchat.other;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.player.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;


/**
 * Temporary wrapper for permission checks, targets yet to be merged fabric-permission-api-v1, making the mod support it before it's finalized.
 * Also contains a fallback when it's not present or it changes and fails to adapt.
 */
public class FabricPermissionBridge {
    public static final boolean IS_LOADED = FabricLoader.getInstance().isModLoaded("fabric-permission-api-v1");
    public static final boolean IS_LOADED_LEGACY = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v1");

    private static MethodHandle permissionCheckCallPlayerLevel;
    private static MethodHandle permissionCheckCallCommandLevel;
    private static MethodHandle permissionCheckCallPlayerBool;
    private static MethodHandle permissionCheckCallCommandBool;
    private static final boolean enabledFabric;

    public static boolean checkPermission(Player player, Identifier permission, PermissionLevel level) {
        if (enabledFabric) {
            try {
                return (boolean) permissionCheckCallPlayerLevel.bindTo(player).invokeExact(permission, level);
            } catch (Throwable e) {
                e.printStackTrace();
                // Should never happen!
            }
        }

        if (IS_LOADED_LEGACY) {
            return Permissions.check(player, permission.toShortLanguageKey(), level);
        }

        return player.permissions().hasPermission(new Permission.HasCommandLevel(level));
    }

    public static boolean checkPermission(CommandSourceStack player, Identifier permission, PermissionLevel level) {
        if (enabledFabric) {
            try {
                return (boolean) permissionCheckCallCommandLevel.bindTo(player).invokeExact(permission, level);
            } catch (Throwable e) {
                e.printStackTrace();
                // Should never happen!
            }
        }

        if (IS_LOADED_LEGACY) {
            return Permissions.check(player, permission.toShortLanguageKey(), level);
        }

        return player.permissions().hasPermission(new Permission.HasCommandLevel(level));
    }

    public static boolean checkPermission(Player player, Identifier permission, boolean defaultValue) {
        if (enabledFabric) {
            try {
                return (boolean) permissionCheckCallPlayerBool.bindTo(player).invokeExact(permission, defaultValue);
            } catch (Throwable e) {
                e.printStackTrace();
                // Should never happen!
            }
        }

        if (IS_LOADED_LEGACY) {
            return Permissions.check(player, permission.toShortLanguageKey(), defaultValue);
        }

        return defaultValue;
    }

    public static boolean checkPermission(CommandSourceStack player, Identifier permission, boolean defaultValue) {
        if (enabledFabric) {
            try {
                return (boolean) permissionCheckCallCommandBool.bindTo(player).invokeExact(permission, defaultValue);
            } catch (Throwable e) {
                e.printStackTrace();
                // Should never happen!
            }
        }

        if (IS_LOADED_LEGACY) {
            return Permissions.check(player, permission.toShortLanguageKey(), defaultValue);
        }

        return defaultValue;
    }

    public static Predicate<CommandSourceStack> require(Identifier permission, PermissionLevel level) {
        return ctx -> checkPermission(ctx, permission, level);
    }

    public static Predicate<CommandSourceStack> require(Identifier permission, boolean def) {
        return ctx -> checkPermission(ctx, permission, def);
    }

    private static MethodHandle findCheckPermission(Class<?> object, Class<?> fallback) throws Throwable {
        var lookup = MethodHandles.publicLookup();
        var meth = object.getMethod("checkPermission", Identifier.class, fallback);
        return permissionCheckCallCommandLevel = lookup.unreflect(meth);
    }

    static {
        var e = false;
        if (IS_LOADED) {
            try {
                permissionCheckCallPlayerLevel = findCheckPermission(Player.class, PermissionLevel.class);
                permissionCheckCallCommandLevel = findCheckPermission(CommandSourceStack.class, PermissionLevel.class);
                permissionCheckCallPlayerBool = findCheckPermission(Player.class, boolean.class);
                permissionCheckCallCommandBool = findCheckPermission(CommandSourceStack.class, boolean.class);
                e = true;
            } catch (Throwable err) {
                err.printStackTrace();
            }
        }
        enabledFabric = e;
    }
}