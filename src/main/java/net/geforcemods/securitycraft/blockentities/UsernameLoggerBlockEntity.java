package net.geforcemods.securitycraft.blockentities;

import java.util.List;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

public class UsernameLoggerBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity, ILockable {
	private static final int TICKS_BETWEEN_ATTACKS = 80;
	private IntOption searchRadius = new IntOption("searchRadius", 3, 1, 20, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private IntOption repeatedLogInterval = new IntOption("repeatedLogInterval", 1, 1, 120, 1);
	private String[] players = new String[100];
	private String[] uuids = new String[100];
	private long[] timestamps = new long[100];
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
				int playerIndex = 0;

				for (int i = 0; i < getPlayers().length && playerIndex < nearbyPlayers.size(); i++) {
					if (getPlayers()[i] == null || getPlayers()[i].equals("")) {
						Player player = nearbyPlayers.get(playerIndex++);

						getPlayers()[i] = player.getName().getString();
						getUuids()[i] = player.getGameProfile().getId().toString();
						getTimestamps()[i] = timestamp;
						changed = true;
					}
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

		for (int i = 0; i < getPlayers().length; i++) {
			if (getPlayers()[i] != null && getPlayers()[i].equals(username) && (getTimestamps()[i] + timeout) > timestamp) //was within the timeout that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		for (int i = 0; i < getPlayers().length; i++) {
			tag.putString("player" + i, getPlayers()[i] == null ? "" : getPlayers()[i]);
			tag.putString("uuid" + i, getUuids()[i] == null ? "" : getUuids()[i]);
			tag.putLong("timestamp" + i, getTimestamps()[i]);
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		for (int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i] = tag.getString("player" + i);
			getUuids()[i] = tag.getString("uuid" + i);
			getTimestamps()[i] = tag.getLong("timestamp" + i);
		}
	}

	public void syncLoggedPlayersToClient() {
		for (int i = 0; i < getPlayers().length; i++) {
			if (getPlayers()[i] != null)
				SecurityCraft.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new UpdateLogger(worldPosition, i, getPlayers()[i], getUuids()[i], getTimestamps()[i]));
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST
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

	public String[] getPlayers() {
		return players;
	}

	public void setPlayers(String[] players) {
		this.players = players;
	}

	public String[] getUuids() {
		return uuids;
	}

	public long[] getTimestamps() {
		return timestamps;
	}
}
