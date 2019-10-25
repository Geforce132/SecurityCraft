package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCClearLogger;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityLogger extends TileEntityOwnable {

	public String[] players = new String[100];

	@Override
	public boolean attackEntity(Entity entity) {
		if (!worldObj.isRemote) {
			addPlayerName(((EntityPlayer) entity).getCommandSenderName());
			sendChangeToClient(false);
		}

		return true;
	}

	@Override
	public boolean canAttack() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	private void addPlayerName(String username){
		if(!hasPlayerName(username))
			for(int i = 0; i < players.length; i++)
				if(players[i] == "" || players[i] == null){
					players[i] = username;
					break;
				}
				else
					continue;
	}

	private boolean hasPlayerName(String username){
		for(int i = 0; i < players.length; i++)
			if(players[i] == username)
				return true;
			else
				continue;

		return false;
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				tag.setString("player" + i, players[i]);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
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
					SecurityCraft.network.sendToAll(new PacketUpdateLogger(xCoord, yCoord, zCoord, i, players[i]));
		}
		else
			SecurityCraft.network.sendToAll(new PacketCClearLogger(xCoord, yCoord, zCoord));
	}

}
