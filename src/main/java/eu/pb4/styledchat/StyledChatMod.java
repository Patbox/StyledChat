package eu.pb4.styledchat;

import com.mojang.serialization.Lifecycle;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.styledchat.command.Commands;
import eu.pb4.styledchat.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Decoration;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class StyledChatMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Styled Chat");
    public static MinecraftServer server = null;
    public static String VERSION = FabricLoader.getInstance().getModContainer("styledchat").get().getMetadata().getVersion().getFriendlyString();

	public static RegistryKey<MessageType> MESSAGE_TYPE = RegistryKey.of(Registry.MESSAGE_TYPE_KEY, new Identifier("styled_chat", "generic_hack"));

	@Override
	public void onInitialize() {
		this.crabboardDetection();
		Commands.register();

		ServerLifecycleEvents.SERVER_STARTING.register((s) -> {
			this.crabboardDetection();
			ConfigManager.loadConfig();
			server = s;
		});

		ServerLifecycleEvents.SERVER_STOPPED.register((s) -> {
			server = null;
		});

		Placeholders.registerChangeEvent((id, removed) -> ConfigManager.clearCached());
	}

	private void crabboardDetection() {
		if (FabricLoader.getInstance().isModLoaded("cardboard")) {
			LOGGER.error("");
			LOGGER.error("Cardboard detected! This mod doesn't work with it!");
			LOGGER.error("You won't get any support as long as it's present!");
			LOGGER.error("");
			LOGGER.error("Read more: https://gist.github.com/Patbox/e44844294c358b614d347d369b0fc3bf");
			LOGGER.error("");
		}
	}
}
