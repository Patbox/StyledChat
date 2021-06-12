package eu.pb4.styledchat;

import eu.pb4.styledchat.command.Commands;
import eu.pb4.styledchat.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class StyledChatMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Styled Chat");
	public static String VERSION = FabricLoader.getInstance().getModContainer("styledchat").get().getMetadata().getVersion().getFriendlyString();

	@Override
	public void onInitialize() {
		Commands.register();
		ServerLifecycleEvents.SERVER_STARTING.register((s) -> ConfigManager.loadConfig());
	}
}
