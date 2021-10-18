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
		this.crabboardDetection();
		Commands.register();
		ServerLifecycleEvents.SERVER_STARTING.register((s) -> {
			this.crabboardDetection();
			ConfigManager.loadConfig();
		});
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
