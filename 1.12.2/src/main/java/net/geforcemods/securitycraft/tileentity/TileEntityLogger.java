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

public class TileEntityLogger extends TileEntityOwnable {

	public String[] players = new String[100];

	@Override
	public boolean attackEntity(Entity entity) {
		if (!world.isRemote) {
			addPlayerName(((EntityPlayer) entity).getName());
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
			addPlayerName(((EntityPlayer)iterator.next()).getName());

		sendChangeToClient(false);
	}

	private void addPlayerName(String username) {
		if(!hasPlayerName(username))
			for(int i = 0; i < players.length; i++)
				if(players[i] == "" || players[i] == null){
					players[i] = username;
					break;
				}
				else
					continue;
	}

	private boolean hasPlayerName(String username) {
		for(int i = 0; i < players.length; i++)
			if(players[i] == username)
				return true;
			else
				continue;

		return false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				tag.setString("player" + i, players[i]);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		for(int i = 0; i < players.length; i++)
			if (tag.hasKey("player" + i))
				players[i] = tag.getString("player" + i);
	}

	public void sendChangeToClient(boolean clear){
		if(!clear)
		{
			for(int i = 0; i < players.length; i++)
				if(players[i] != null)
					SecurityCraft.network.sendToAll(new PacketUpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, players[i]));
		}
		else
			SecurityCraft.network.sendToAll(new PacketCClearLogger(pos));
	}

}
