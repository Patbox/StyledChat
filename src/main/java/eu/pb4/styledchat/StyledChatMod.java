package eu.pb4.styledchat;

import com.mojang.serialization.Lifecycle;
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

		BuiltinRegistries.add(BuiltinRegistries.MESSAGE_TYPE, MESSAGE_TYPE,
				new MessageType(Optional.of(new MessageType.DisplayRule(Optional.of(Decoration.ofChat("%s")))),
						Optional.empty(),
						Optional.of(MessageType.NarrationRule.of(Decoration.ofChat("%s"), MessageType.NarrationRule.Kind.CHAT)))
		);

		ServerLifecycleEvents.SERVER_STARTING.register((s) -> {
			this.crabboardDetection();
			server = s;
		});

		ServerLifecycleEvents.SERVER_STARTED.register((s) -> {
			ConfigManager.loadConfig();
		});

		ServerLifecycleEvents.SERVER_STOPPED.register((s) -> {
			server = null;
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
