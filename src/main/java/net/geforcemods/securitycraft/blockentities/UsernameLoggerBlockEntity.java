package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class UsernameLoggerBlockEntity extends DisguisableBlockEntity implements ITickable, ILockable {
	private static final int TICKS_BETWEEN_ATTACKS = 80;
	private IntOption searchRadius = new IntOption(this::getPos, "searchRadius", 3, 1, 20, 1);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private IntOption repeatedLogInterval = new IntOption(this::getBlockPos, "repeatedLogInterval", 1, 1, 120, 1);
	private String[] players = new String[100];
	private String[] uuids = new String[100];
	private long[] timestamps = new long[100];
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (isDisabled())
				return;

			if (cooldown > 0)
				cooldown--;
			else if (world.getRedstonePowerFromNeighbors(pos) > 0) {
				long timestamp = System.currentTimeMillis();
				List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(searchRadius.get()), e -> !e.isSpectator() && !(isOwnedBy(e) && ignoresOwner() || isAllowed(e)) && !respectInvisibility.isConsideredInvisible(e) && !wasPlayerRecentlyAdded(e.getName(), timestamp));

				if (!nearbyPlayers.isEmpty()) {
					boolean changed = false;
					int playerIndex = 0;

					for (int i = 0; i < getPlayers().length && playerIndex < nearbyPlayers.size(); i++) {
						if (getPlayers()[i] == null || getPlayers()[i].equals("")) {
							EntityPlayer player = nearbyPlayers.get(playerIndex++);

							getPlayers()[i] = player.getName();
							getUuids()[i] = player.getGameProfile().getId().toString();
							getTimestamps()[i] = timestamp;
							changed = true;
						}
					}

					if (changed) {
						markDirty();
						syncLoggedPlayersToClient();
					}
				}

				cooldown = TICKS_BETWEEN_ATTACKS;
			}
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		for (int i = 0; i < getPlayers().length; i++) {
			tag.setString("player" + i, getPlayers()[i] == null ? "" : getPlayers()[i]);
			tag.setString("uuid" + i, getUuids()[i] == null ? "" : getUuids()[i]);
			tag.setLong("timestamp" + i, getTimestamps()[i]);
		}

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		for (int i = 0; i < getPlayers().length; i++) {
			getPlayers()[i] = tag.getString("player" + i);
			getUuids()[i] = tag.getString("uuid" + i);
			getTimestamps()[i] = tag.getLong("timestamp" + i);
		}
	}

	public void syncLoggedPlayersToClient() {
		for (int i = 0; i < getPlayers().length; i++) {
			if (getPlayers()[i] != null)
				SecurityCraft.network.sendToAllTracking(new UpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, getPlayers()[i], getUuids()[i], getTimestamps()[i]), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
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

	public void setUuids(String[] uuids) {
		this.uuids = uuids;
	}

	public long[] getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(long[] timestamps) {
		this.timestamps = timestamps;
	}
}
