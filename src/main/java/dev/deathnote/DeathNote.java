package dev.deathnote;

import dev.deathnote.item.ModItemGroups;
import dev.deathnote.item.ModItems;
import dev.deathnote.network.DeathNoteEffectPayload;
import dev.deathnote.network.DeathNoteVictimPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class DeathNote implements ModInitializer {
	public static final String MOD_ID = "deathnote";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final double MAX_TARGET_DISTANCE = 128.0;
	private static final int DEATH_DELAY_TICKS = 100;
	private static final double SMOKE_Y_OFFSET = 1.0;

	private static final List<PendingDeath> PENDING_DEATHS = new ArrayList<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Connecting with the Shinigami Realm..");
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();

		PayloadTypeRegistry.playC2S().register(DeathNoteEffectPayload.ID, DeathNoteEffectPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(DeathNoteVictimPayload.ID, DeathNoteVictimPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(DeathNoteEffectPayload.ID, DeathNote::receiveEffect);
		ServerTickEvents.END_SERVER_TICK.register(DeathNote::tickPendingDeaths);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> PENDING_DEATHS.clear());
	}

	private static void receiveEffect(DeathNoteEffectPayload payload, ServerPlayNetworking.Context context) {
		ServerPlayerEntity writer = context.player();
		if (!isHoldingDeathNote(writer)) {
			return;
		}
		//? if >=1.20.6 {
		MinecraftServer server = context.server();
		//?} else {
		/*MinecraftServer server = writer.getServer();
		*///?}
		if (server == null) {
			return;
		}

		String victimName = payload.victimName().trim();
		if (victimName.isEmpty()) {
			return;
		}
		applyEffect(server, writer, victimName);
	}

	private static boolean isHoldingDeathNote(PlayerEntity player) {
		return player.getMainHandStack().isOf(ModItems.DEATH_NOTE_ITEM)
				|| player.getOffHandStack().isOf(ModItems.DEATH_NOTE_ITEM);
	}

	private static void applyEffect(MinecraftServer server, ServerPlayerEntity writer, String victimName) {
		ServerWorld writerWorld = getServerWorld(server, writer);
		if (writerWorld == null) {
			return;
		}

		ServerPlayerEntity victim = server.getPlayerManager().getPlayer(victimName);
		if (victim != null) {
			ServerWorld victimWorld = getServerWorld(server, victim);
			if (victimWorld == null) {
				return;
			}

			notifyVictim(victim);
			playThunder(victimWorld, position(victim));
			PENDING_DEATHS.add(new PendingDeath(victim.getUuid(), server.getTicks() + DEATH_DELAY_TICKS));
			return;
		}

		LivingEntity target = findLookedAtMob(writer, writerWorld);
		if (target == null) {
			return;
		}

		playThunder(writerWorld, position(target));
		spawnSmoke(writerWorld, position(target));
		kill(target, writerWorld);
	}

	private static void tickPendingDeaths(MinecraftServer server) {
		if (PENDING_DEATHS.isEmpty()) {
			return;
		}

		int now = server.getTicks();
		Iterator<PendingDeath> iterator = PENDING_DEATHS.iterator();
		while (iterator.hasNext()) {
			PendingDeath pending = iterator.next();
			if (now < pending.dueTick()) {
				continue;
			}
			iterator.remove();

			ServerPlayerEntity victim = server.getPlayerManager().getPlayer(pending.victim());
			if (victim == null) {
				continue;
			}
			ServerWorld world = getServerWorld(server, victim);
			if (world == null) {
				continue;
			}

			spawnSmoke(world, position(victim));
			kill(victim, world);
		}
	}

	private static void kill(Entity entity, ServerWorld world) {
		//? if >=1.21.2 {
		/*entity.kill(world);
		*///?} else {
		entity.kill();
		//?}
	}

	private static void notifyVictim(ServerPlayerEntity victim) {
		if (ServerPlayNetworking.canSend(victim, DeathNoteVictimPayload.ID)) {
			ServerPlayNetworking.send(victim, new DeathNoteVictimPayload());
		}
	}

	private static void playThunder(ServerWorld world, Vec3d pos) {
		world.playSound(null, pos.x, pos.y, pos.z,
				SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}

	private static void spawnSmoke(ServerWorld world, Vec3d pos) {
		world.spawnParticles(ParticleTypes.SMOKE, pos.x, pos.y + SMOKE_Y_OFFSET, pos.z,
				100, 0.5, 0.5, 0.5, 0.1);
	}

	private static Vec3d position(Entity entity) {
		return new Vec3d(entity.getX(), entity.getY(), entity.getZ());
	}

	/**
	 * Finds the living, non-player mob the writer is aiming at, mirroring vanilla's
	 * crosshair targeting: cast the look vector from the eyes, pick the closest
	 * matching entity hitbox, then reject it if a block is in the way.
	 */
	private static LivingEntity findLookedAtMob(ServerPlayerEntity writer, ServerWorld world) {
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

	private static ServerWorld getServerWorld(MinecraftServer server, ServerPlayerEntity player) {
		for (ServerWorld world : server.getWorlds()) {
			if (world.getPlayers().contains(player)) {
				return world;
			}
		}
		return null;
	}

	private record PendingDeath(UUID victim, int dueTick) {
	}
}
