package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCClearLogger;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityLogger extends TileEntityDisguisable {

	public String[] players = new String[100];
	public String[] uuids = new String[100];
	public long[] timestamps = new long[100];

	@Override
	public boolean attackEntity(Entity entity) {
		if (!world.isRemote && entity instanceof EntityPlayer) {
			addPlayer((EntityPlayer)entity);
			sendChangeToClient(false);
		}

		return true;
	}

	@Override
	public boolean canAttack() {
		return world.getRedstonePowerFromNeighbors(pos) > 0;
	}

	public void logPlayers(){
		double range = ConfigHandler.usernameLoggerSearchRadius;

		AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).grow(range, range, range);
		List<?> entities = world.getEntitiesWithinAABB(EntityPlayer.class, area);
		Iterator<?> iterator = entities.iterator();

		while(iterator.hasNext())
			addPlayer((EntityPlayer)iterator.next());

		sendChangeToClient(false);
	}

	private void addPlayer(EntityPlayer player) {
		long timestamp = System.currentTimeMillis();

		if(!getOwner().isOwner(player) && !hasPlayerName(player.getName(), timestamp))
		{
			for(int i = 0; i < players.length; i++)
			{
				if(players[i] == "" || players[i] == null){
					players[i] = player.getName();
					uuids[i] = player.getGameProfile().getId().toString();
					timestamps[i] = timestamp;
					break;
				}
			}
		}
	}

	private boolean hasPlayerName(String username, long timestamp) {
		for(int i = 0; i < players.length; i++)
		{
			if(players[i] != null && players[i].equals(username) && (timestamps[i] + 1000L) > timestamp) //was within the last second that the same player was last added
				return true;
		}

		return false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		for(int i = 0; i < players.length; i++)
		{
			if(players[i] != null)
			{
				tag.setString("player" + i, players[i] == null ? "" : players[i]);
				tag.setString("uuid" + i, uuids[i] == null ? "" : uuids[i]);
				tag.setLong("timestamp" + i, timestamps[i]);
			}
		}

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		for(int i = 0; i < players.length; i++)
		{
			if(tag.hasKey("player" + i))
				players[i] = tag.getString("player" + i);

			if(tag.hasKey("uuid" + i))
				uuids[i] = tag.getString("uuid" + i);

			if(tag.hasKey("timestamp" + i))
				timestamps[i] = tag.getLong("timestamp" + i);
		}
	}

	public void sendChangeToClient(boolean clear){
		if(!clear)
		{
			for(int i = 0; i < players.length; i++)
			{
				if(players[i] != null)
					SecurityCraft.network.sendToAll(new PacketUpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, players[i], uuids[i], timestamps[i]));
			}
		}
		else
			SecurityCraft.network.sendToAll(new PacketCClearLogger(pos));
	}

}
