//DeathNote.java
package dev.deathnote;

import dev.deathnote.item.ModItemGroups;
import dev.deathnote.item.ModItems;
import dev.deathnote.item.custom.CustomBookEditScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathNote implements ModInitializer {
	public static final String MOD_ID = "deathnote";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Connecting with the Shinigami Realm..");
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();

		PayloadTypeRegistry.playC2S().register(CustomBookEditScreen.DeathNoteEffectPayload.ID, CustomBookEditScreen.DeathNoteEffectPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(CustomBookEditScreen.DeathNoteEffectPayload.ID, (payload, context) -> {
			String victimName = payload.victimName();
			// `Context.server()` does not exist on 1.20.5, and `Entity#getServer` was
			// removed in 1.21.9, so pick whichever API the version provides.
			//? if >=1.20.6 {
			MinecraftServer server = context.server();
			//?} else {
			/*MinecraftServer server = context.player().getServer();
			*///?}
			if (server == null) {
				return;
			}
			server.execute(() -> {
				if (server.getPlayerManager().getPlayer(victimName) != null) {
					ServerCommandSource source = server.getCommandSource().withSilent();
					executeCommand(server, source, "title " + victimName + " times 20 60 20");
					executeCommand(server, source, "title " + victimName + " subtitle {\"text\":\"Death Note claims another soul...\",\"color\":\"red\"}");
					executeCommand(server, source, "title " + victimName + " title {\"text\":\"Your name was written\",\"color\":\"dark_red\"}");
					executeCommand(server, source, "execute at " + victimName + " run playsound minecraft:entity.lightning_bolt.thunder player @a ~ ~ ~ 1 1");
					new Thread(() -> {
						try {
							Thread.sleep(5000);
							executeCommand(server, source, "execute at " + victimName + " run particle minecraft:smoke ~ ~1 ~ 0.5 0.5 0.5 0.1 100");
							executeCommand(server, source, "kill " + victimName);
						} catch (InterruptedException e) {
							LOGGER.error("Death Note effect interrupted", e);
						}
					}).start();
				} else {
					ServerCommandSource source = server.getCommandSource();
					executeCommand(server, source, "execute at " + "@e[type=minecraft:" + victimName.toLowerCase() + ",sort=nearest,limit=1]" + " run playsound minecraft:entity.lightning_bolt.thunder player @a ~ ~ ~ 1 1");
					executeCommand(server, source, "kill @e[type=minecraft:" + victimName.toLowerCase() + ",sort=nearest,limit=1]");
				}
			});
		});
	}

	private static void executeCommand(MinecraftServer server, ServerCommandSource source, String command) {
		//? if >=1.21.10 {
		/*server.getCommandManager().parseAndExecute(source, command);
		*///?} else {
		server.getCommandManager().executeWithPrefix(source, command);
		//?}
	}
}
