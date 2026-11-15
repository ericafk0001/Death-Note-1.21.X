package dev.deathnote;

import dev.deathnote.item.ModItemGroups;
import dev.deathnote.item.ModItems;
import dev.deathnote.network.DeathNoteBookPayload;
import dev.deathnote.network.DeathNoteEffectPayload;
import dev.deathnote.network.DeathNoteVictimPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.function.Predicate;

public class DeathNote implements ModInitializer {
	public static final String MOD_ID = "deathnote";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final double MAX_TARGET_DISTANCE = 128.0;
	private static final int DAMAGE_HITS = 10;
	private static final int DAMAGE_INTERVAL_TICKS = 10;
	private static final double SMOKE_Y_OFFSET = 1.0;

	private static final List<PendingDamage> PENDING_DAMAGE = new ArrayList<>();

	@Override
	public void onInitialize() {
		LOGGER.info("Connecting with the Shinigami Realm..");
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();

		PayloadTypeRegistry.playC2S().register(DeathNoteEffectPayload.ID, DeathNoteEffectPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DeathNoteBookPayload.ID, DeathNoteBookPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(DeathNoteVictimPayload.ID, DeathNoteVictimPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(DeathNoteEffectPayload.ID, DeathNote::receiveEffect);
		ServerPlayNetworking.registerGlobalReceiver(DeathNoteBookPayload.ID, DeathNote::receiveBook);
		ServerTickEvents.END_SERVER_TICK.register(DeathNote::tickPendingDamage);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> PENDING_DAMAGE.clear());
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

	private static void receiveBook(DeathNoteBookPayload payload, ServerPlayNetworking.Context context) {
		// Vanilla ignores custom books here on 1.20.x and 1.21.1, so the pages
		// would only live on the client until the next inventory sync.
		Hand hand = payload.hand() == Hand.OFF_HAND.ordinal() ? Hand.OFF_HAND : Hand.MAIN_HAND;
		ItemStack book = context.player().getStackInHand(hand);
		if (!book.isOf(ModItems.DEATH_NOTE_ITEM)) {
			return;
		}

		List<RawFilteredPair<String>> pages = payload.pages().stream()
				.limit(WritableBookContentComponent.MAX_PAGE_COUNT)
				.map(RawFilteredPair::of)
				.toList();
		book.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, new WritableBookContentComponent(pages));
	}

	private static boolean isHoldingDeathNote(PlayerEntity player) {
		return player.getMainHandStack().isOf(ModItems.DEATH_NOTE_ITEM)
				|| player.getOffHandStack().isOf(ModItems.DEATH_NOTE_ITEM);
	}

	private static ServerPlayerEntity findPlayer(MinecraftServer server, String name) {
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			if (player.getName().getString().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;
	}

	private static boolean matchesName(Entity entity, String name) {
		if (entity.getName().getString().equalsIgnoreCase(name)) {
			return true;
		}
		Identifier id = EntityType.getId(entity.getType());
		return id != null && id.getPath().equalsIgnoreCase(name);
	}

	private static void applyEffect(MinecraftServer server, ServerPlayerEntity writer, String victimName) {
		ServerPlayerEntity player = findPlayer(server, victimName);

		if (player != null) {
			ServerWorld world = getServerWorld(server, player);
			if (world == null) {
				return;
			}
			notifyVictim(player);
			startDamage(server, world, player);
			return;
		}

		ServerWorld writerWorld = getServerWorld(server, writer);
		if (writerWorld == null) {
			return;
		}
		LivingEntity target = findLookedAtMob(writer, writerWorld);
		if (target == null || !matchesName(target, victimName)) {
			return;
		}
		startDamage(server, writerWorld, target);
	}

	private static void startDamage(MinecraftServer server, ServerWorld world, LivingEntity victim) {
		playThunder(world, position(victim));
		spawnSmoke(world, position(victim), 60);

		float damagePerHit = victim.getMaxHealth() / DAMAGE_HITS;
		PENDING_DAMAGE.removeIf(pending -> pending.victim().equals(victim.getUuid()));
		PENDING_DAMAGE.add(new PendingDamage(victim.getUuid(), damagePerHit, DAMAGE_HITS, server.getTicks()));
	}

	private static void tickPendingDamage(MinecraftServer server) {
		if (PENDING_DAMAGE.isEmpty()) {
			return;
		}

		int now = server.getTicks();
		ListIterator<PendingDamage> iterator = PENDING_DAMAGE.listIterator();
		while (iterator.hasNext()) {
			PendingDamage pending = iterator.next();
			if (now < pending.nextHitTick()) {
				continue;
			}

			ServerWorld world = null;
			LivingEntity victim = null;
			for (ServerWorld candidate : server.getWorlds()) {
				if (candidate.getEntity(pending.victim()) instanceof LivingEntity living) {
					world = candidate;
					victim = living;
					break;
				}
			}
			if (victim == null) {
				iterator.remove();
				continue;
			}

			boolean lastHit = pending.hitsLeft() <= 1;
			float amount = lastHit
					? victim.getHealth() + victim.getAbsorptionAmount() + 1.0F
					: pending.damagePerHit();

			// Vanilla's hurt cooldown eats rapid hits, so clear it before every one.
			victim.timeUntilRegen = 0;
			//? if >=1.21.2 {
			/*victim.damage(world, world.getDamageSources().magic(), amount);
			*///?} else {
			victim.damage(world.getDamageSources().magic(), amount);
			//?}
			spawnSmoke(world, position(victim), 12);

			if (!victim.isAlive()) {
				iterator.remove();
				continue;
			}
			if (lastHit) {
				// Protection or absorption can soak the last hit; the name still wins.
				//? if >=1.21.2 {
				/*victim.kill(world);
				*///?} else {
				victim.kill();
				//?}
				iterator.remove();
				continue;
			}

			iterator.set(new PendingDamage(pending.victim(), pending.damagePerHit(),
					pending.hitsLeft() - 1, now + DAMAGE_INTERVAL_TICKS));
		}
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

	private static void spawnSmoke(ServerWorld world, Vec3d pos, int count) {
		world.spawnParticles(ParticleTypes.SMOKE, pos.x, pos.y + SMOKE_Y_OFFSET, pos.z,
				count, 0.4, 0.5, 0.4, 0.02);
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

	private record PendingDamage(UUID victim, float damagePerHit, int hitsLeft, int nextHitTick) {
	}
}
