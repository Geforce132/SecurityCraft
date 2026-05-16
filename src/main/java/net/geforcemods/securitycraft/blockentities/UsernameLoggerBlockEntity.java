package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

public class UsernameLoggerBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity, ILockable {
	public static final int LOGGER_LIST_SIZE = 100;
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
					boolean wasPlayerAdded = false;

					if (isModuleEnabled(ModuleType.SMART)) {
						for (int i = LOGGER_LIST_SIZE - 1; i >= 0; i--) { //Loop the entry list from back to front, overwriting the bottommost entry instead of the topmost one
							UsernameLoggerEntry entry = getEntry(i);

							if (entry != null && entry.playerName.equals(nearbyPlayerName)) {
								getEntries()[i] = new UsernameLoggerEntry(nearbyPlayerName, nearbyPlayer.getGameProfile().id().toString(), timestamp);
								wasPlayerAdded = true;
								break;
							}
						}
					}

					if (!wasPlayerAdded) {
						if (nextEmptyIndex < 0) {
							for (int i = 0; i < LOGGER_LIST_SIZE; i++) {
								if (getEntries()[i] == null) {
									nextEmptyIndex = i;
									break;
								}
							}
						}

						if (nextEmptyIndex >= 0 && nextEmptyIndex < LOGGER_LIST_SIZE) { //The index is still -1 if the list is full when the index is determined
							getEntries()[nextEmptyIndex++] = new UsernameLoggerEntry(nearbyPlayerName, nearbyPlayer.getGameProfile().id().toString(), timestamp);
							wasPlayerAdded = true;
						}
					}

					if (wasPlayerAdded)
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

	private boolean wasPlayerRecentlyAdded(String username, long timestamp) {
		long timeout = repeatedLogInterval.get() * 1000L;

		for (UsernameLoggerEntry entry : getEntries()) {
			if (entry != null && entry.playerName.equals(username) && (entry.timestamp + timeout) > timestamp) //was within the timeout that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		for (int i = 0; i < LOGGER_LIST_SIZE; i++) {
			UsernameLoggerEntry entry = getEntry(i);

			if (entry != null) {
				tag.putString("player" + i, entry.playerName);
				tag.putString("uuid" + i, entry.uuid);
				tag.putLong("timestamp" + i, entry.timestamp);
			}
		}
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		clearEntries();

		for (int i = 0; i < LOGGER_LIST_SIZE; i++) {
			String playerName = tag.getStringOr("player" + i, "");

			if (!playerName.isEmpty()) {
				String uuid = tag.getStringOr("uuid" + i, "");
				long timestamp = tag.getLongOr("timestamp" + i, 0);

				getEntries()[i] = new UsernameLoggerEntry(playerName, uuid, timestamp);
			}
		}
	}

	public void syncLoggedPlayersToClient() {
		for (int i = 0; i < LOGGER_LIST_SIZE; i++) {
			UsernameLoggerEntry entry = getEntry(i);

			if (entry != null)
				PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, ChunkPos.containing(worldPosition), new UpdateLogger(worldPosition, i, entry.playerName, entry.uuid, entry.timestamp));
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

	public record UsernameLoggerEntry(String playerName, String uuid, long timestamp) {}
}
