package eu.pb4.styledchat;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.styledchat.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Decoration;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class StyledChatMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Styled Chat");
    public static MinecraftServer server = null;
    public static String VERSION = FabricLoader.getInstance().getModContainer("styledchat").get().getMetadata().getVersion().getFriendlyString();

	public static boolean USE_FABRIC_API = true;

	public static RegistryKey<MessageType> MESSAGE_TYPE_ID = RegistryKey.of(Registry.MESSAGE_TYPE_KEY, new Identifier("styled_chat", "generic_hack"));
	public static MessageType MESSAGE_TYPE = new MessageType(Decoration.ofChat("%s"), Decoration.ofChat("%s"));

	@Override
	public void onInitialize() {
		this.crabboardDetection();
		Placeholders.registerChangeEvent((id, removed) -> ConfigManager.clearCached());
	}


	public static void serverStarting(MinecraftServer s) {
		crabboardDetection();
		ConfigManager.loadConfig();
		server = s;
	}

	public static void serverStopped(MinecraftServer s) {
		server = null;
	}


	private static void crabboardDetection() {
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
