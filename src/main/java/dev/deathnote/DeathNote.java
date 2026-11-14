//DeathNote.java
package dev.deathnote;

import dev.deathnote.item.ModItemGroups;
import dev.deathnote.item.ModItems;
import dev.deathnote.item.custom.CustomBookEditScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class DeathNote implements ModInitializer {
	public static final String MOD_ID = "deathnote";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final double MAX_TARGET_DISTANCE = 128.0;

	@Override
	public void onInitialize() {
		LOGGER.info("Connecting with the Shinigami Realm..");
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();

		PayloadTypeRegistry.playC2S().register(CustomBookEditScreen.DeathNoteEffectPayload.ID, CustomBookEditScreen.DeathNoteEffectPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(CustomBookEditScreen.DeathNoteEffectPayload.ID, (payload, context) -> {
			String victimName = payload.victimName();
			ServerPlayerEntity writer = context.player();
			//? if >=1.20.6 {
			MinecraftServer server = context.server();
			//?} else {
			/*MinecraftServer server = writer.getServer();
			*///?}
			if (server == null) {
				return;
			}
			server.execute(() -> {
				ServerCommandSource source = silentCommandSource(server);
				if (server.getPlayerManager().getPlayer(victimName) != null) {
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
					LivingEntity target = findLookedAtMob(writer);
					if (target != null) {
						String targetId = target.getUuidAsString();
						executeCommand(server, source, "execute at " + targetId + " run playsound minecraft:entity.lightning_bolt.thunder player @a ~ ~ ~ 1 1");
						executeCommand(server, source, "execute at " + targetId + " run particle minecraft:smoke ~ ~1 ~ 0.5 0.5 0.5 0.1 100");
						executeCommand(server, source, "kill " + targetId);
					}
				}
			});
		});
	}

	/**
	 * Finds the living, non-player mob the writer is aiming at, mirroring vanilla's
	 * crosshair targeting: cast the look vector from the eyes, pick the closest
	 * matching entity hitbox, then reject it if a block is in the way.
	 */
	private static LivingEntity findLookedAtMob(ServerPlayerEntity writer) {
		//? if >=1.21.10 {
		/*ServerWorld world = writer.getEntityWorld();
		*///?} else {
		ServerWorld world = writer.getServerWorld();
		//?}

		Vec3d start = writer.getCameraPosVec(1.0F);
		Vec3d direction = writer.getRotationVec(1.0F);
		Vec3d end = start.add(direction.multiply(MAX_TARGET_DISTANCE));
		Box searchBox = writer.getBoundingBox().stretch(direction.multiply(MAX_TARGET_DISTANCE)).expand(1.0);

		Predicate<Entity> filter = entity -> entity instanceof LivingEntity living
				&& entity != writer
				&& !(entity instanceof PlayerEntity)
				&& living.isAlive()
				&& !entity.isSpectator()
				&& entity.canHit();

		EntityHitResult entityHit = ProjectileUtil.raycast(writer, start, end, searchBox, filter,
				MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE);
		if (entityHit == null) {
			return null;
		}

		BlockHitResult blockHit = world.raycast(new RaycastContext(start, entityHit.getPos(),
				RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, writer));
		if (blockHit.getType() == HitResult.Type.BLOCK
				&& start.squaredDistanceTo(blockHit.getPos()) < start.squaredDistanceTo(entityHit.getPos())) {
			return null;
		}

		return (LivingEntity) entityHit.getEntity();
	}

	private static ServerCommandSource silentCommandSource(MinecraftServer server) {
		// The console source reports that it cannot be silenced, so withSilent() alone
		// leaves feedback broadcasts (and the op chat spam) enabled. Replace its output
		// with a no-op first, then mark the resulting source silent.
		return server.getCommandSource()
				.withOutput(CommandOutput.DUMMY)
				.withSilent();
	}

	private static void executeCommand(MinecraftServer server, ServerCommandSource source, String command) {
		//? if >=1.21.10 {
		/*server.getCommandManager().parseAndExecute(source, command);
		*///?} else {
		server.getCommandManager().executeWithPrefix(source, command);
		//?}
	}
}
