package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.network.client.UpdateLogger;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityLogger extends TileEntityDisguisable implements ITickable, ILockable {
	private static final int TICKS_BETWEEN_ATTACKS = 80;
	private OptionInt searchRadius = new OptionInt(this::getPos, "searchRadius", 3, 1, 20, 1, true);
	private DisabledOption disabled = new DisabledOption(false);
	public String[] players = new String[100];
	public String[] uuids = new String[100];
	public long[] timestamps = new long[100];
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (isDisabled() || cooldown-- > 0)
				return;

			if (world.getRedstonePowerFromNeighbors(pos) > 0) {
				world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(searchRadius.get()), e -> !e.isSpectator()).forEach(this::addPlayer);
				syncLoggedPlayersToClient();
			}

			cooldown = TICKS_BETWEEN_ATTACKS;
		}
	}

	public void addPlayer(EntityPlayer player) {
		String playerName = player.getName();
		long timestamp = System.currentTimeMillis();

		if (!getOwner().isOwner(player) && !EntityUtils.isInvisible(player) && !wasPlayerRecentlyAdded(playerName, timestamp)) {
			//ignore players on the allowlist
			if (isAllowed(player))
				return;

			for (int i = 0; i < players.length; i++) {
				if (players[i] == null || players[i].equals("")) {
					players[i] = player.getName();
					uuids[i] = player.getGameProfile().getId().toString();
					timestamps[i] = timestamp;
					break;
				}
			}
		}
	}

	private boolean wasPlayerRecentlyAdded(String username, long timestamp) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null && players[i].equals(username) && (timestamps[i] + 1000L) > timestamp) //was within the last second that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		for (int i = 0; i < players.length; i++) {
			tag.setString("player" + i, players[i] == null ? "" : players[i]);
			tag.setString("uuid" + i, uuids[i] == null ? "" : uuids[i]);
			tag.setLong("timestamp" + i, timestamps[i]);
		}

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		for (int i = 0; i < players.length; i++) {
			players[i] = tag.getString("player" + i);
			uuids[i] = tag.getString("uuid" + i);
			timestamps[i] = tag.getLong("timestamp" + i);
		}
	}

	public void syncLoggedPlayersToClient() {
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null)
				SecurityCraft.network.sendToAllTracking(new UpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, players[i], uuids[i], timestamps[i]), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0));
		}
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {
				EnumModuleType.DISGUISE, EnumModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				searchRadius, disabled
		};
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
