package net.geforcemods.securitycraft.blockentities;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

public class UsernameLoggerBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity, ILockable {
	public static final int LOGGER_LIST_SIZE = 128;
	private static final int TICKS_BETWEEN_ATTACKS = 80;
	private final IntOption searchRadius = new IntOption("searchRadius", 3, 1, 20, 1);
	private final DisabledOption disabled = new DisabledOption(false);
	private final IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private final RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private final IntOption repeatedLogInterval = new IntOption("repeatedLogInterval", 1, 1, 120, 1);
	private UsernameLoggerEntry[] entries = new UsernameLoggerEntry[LOGGER_LIST_SIZE];
	private int nextEmptyIndex = -1;
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	public UsernameLoggerBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (isDisabled())
			return;

		if (cooldown > 0)
			cooldown--;
		else if (level.getBestNeighborSignal(pos) > 0) {
			long timestamp = System.currentTimeMillis();
			List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(searchRadius.get()), e -> e.canBeSeenByAnyone() && !(isOwnedBy(e) && ignoresOwner() || isAllowed(e)) && !respectInvisibility.isConsideredInvisible(e) && !wasPlayerRecentlyAdded(e.getName().getString(), timestamp));

			if (!nearbyPlayers.isEmpty()) {
				boolean changed = false;

				for (Player nearbyPlayer : nearbyPlayers) {
					String nearbyPlayerName = nearbyPlayer.getName().getString();

					if (isModuleEnabled(ModuleType.SMART) && overrideLastEntry(nearbyPlayer, nearbyPlayerName, timestamp))
						changed = true;
					else if (addEntry(nearbyPlayer, nearbyPlayerName, timestamp))
						changed = true;
				}

				if (changed) {
					setChanged();
					syncLoggedPlayersToClient();
				}
			}

			cooldown = TICKS_BETWEEN_ATTACKS;
		}
	}

	public boolean overrideLastEntry(Player nearbyPlayer, String nearbyPlayerName, long timestamp) {
		//Loop the entry list from back to front, overwriting the bottommost entry instead of the topmost one
		for (int i = LOGGER_LIST_SIZE - 1; i >= 0; i--) {
			UsernameLoggerEntry entry = getEntry(i);

			if (entry != null && entry.playerName.equals(nearbyPlayerName)) {
				getEntries()[i] = new UsernameLoggerEntry(nearbyPlayerName, nearbyPlayer.getGameProfile().getId(), timestamp);
				return true;
			}
		}

		return false;
	}

	public boolean addEntry(Player nearbyPlayer, String nearbyPlayerName, long timestamp) {
		updateNextEmptyIndex();

		//The index is still -1 if the list is full when the index is determined
		if (nextEmptyIndex >= 0 && nextEmptyIndex < LOGGER_LIST_SIZE) {
			getEntries()[nextEmptyIndex++] = new UsernameLoggerEntry(nearbyPlayerName, nearbyPlayer.getGameProfile().getId(), timestamp);
			return true;
		}

		return false;
	}

	private void updateNextEmptyIndex() {
		if (nextEmptyIndex < 0) {
			for (int i = 0; i < LOGGER_LIST_SIZE; i++) {
				if (getEntries()[i] == null) {
					nextEmptyIndex = i;
					break;
				}
			}
		}
	}

	private boolean wasPlayerRecentlyAdded(String username, long timestamp) {
		long timeout = repeatedLogInterval.get() * 1000L;

		for (UsernameLoggerEntry entry : getEntries()) {
			if (entry != null && entry.playerName.equals(username) && (entry.timestamp + timeout) > timestamp) //was within the timeout that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		ListTag players = new ListTag();

		for (UsernameLoggerEntry entry : entries) {
			if (entry != null)
				players.add(UsernameLoggerEntry.CODEC.encodeStart(NbtOps.INSTANCE, entry).getOrThrow(false, error -> {}));
		}

		tag.put("players", players);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		clearEntries();

		if (tag.contains("players")) {
			ListTag players = tag.getList("players", Tag.TAG_COMPOUND);
			int i = 0;

			for (Tag entry : players) {
				try {
					Optional<UsernameLoggerEntry> optional = UsernameLoggerEntry.CODEC.decode(NbtOps.INSTANCE, entry).result().map(Pair::getFirst);

					if (optional.isPresent())
						entries[i++] = optional.get();
				}
				catch (Exception exception) {
					SecurityCraft.LOGGER.error("Failed to load Username Logger list entry at position {}: {}\n{}", worldPosition, entry, exception);
				}

				if (i == LOGGER_LIST_SIZE)
					break;
			}
		}
		else {
			//Legacy loading
			for (int i = 0; i < LOGGER_LIST_SIZE; i++) {
				String playerName = tag.getString("player" + i);

				if (!playerName.isEmpty()) {
					String uuidAsString = tag.getString("uuid" + i);
					UUID uuid = !uuidAsString.isEmpty() ? UUID.fromString(uuidAsString) : UUID.randomUUID();
					long timestamp = tag.getLong("timestamp" + i);

					entries[i] = new UsernameLoggerEntry(playerName, uuid, timestamp);
				}
			}
		}
	}

	public void syncLoggedPlayersToClient() {
		for (int i = 0; i < LOGGER_LIST_SIZE; i++) {
			UsernameLoggerEntry entry = getEntry(i);

			if (entry != null)
				SecurityCraft.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new UpdateLogger(worldPosition, i, entry.playerName, entry.uuid, entry.timestamp));
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST, ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				searchRadius, disabled, ignoreOwner, respectInvisibility, repeatedLogInterval
		};
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public UsernameLoggerEntry[] getEntries() {
		return entries;
	}

	public UsernameLoggerEntry getEntry(int index) {
		if (index >= LOGGER_LIST_SIZE)
			return null;

		return getEntries()[index];
	}

	public void clearEntries() {
		entries = new UsernameLoggerEntry[LOGGER_LIST_SIZE];
		nextEmptyIndex = -1;
	}

	public record UsernameLoggerEntry(String playerName, UUID uuid, long timestamp) {
		public static final Codec<UsernameLoggerEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.STRING.fieldOf("player_name").forGetter(UsernameLoggerEntry::playerName),
				UUIDUtil.CODEC.fieldOf("uuid").forGetter(UsernameLoggerEntry::uuid),
				Codec.LONG.fieldOf("timestamp").forGetter(UsernameLoggerEntry::timestamp)
		).apply(i, UsernameLoggerEntry::new));
	}
}
