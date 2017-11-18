package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityLogger extends TileEntityOwnable {

	public String[] players = new String[100];

	@Override
	public boolean attackEntity(Entity entity) {
		if (!worldObj.isRemote) {
			addPlayerName(((EntityPlayer) entity).getCommandSenderName());
			sendChangeToClient();
		}

		return true;
	}

	@Override
	public boolean canAttack() {
		return worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	public void logPlayers(){
		double d0 = (mod_SecurityCraft.configHandler.usernameLoggerSearchRadius);

		AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(d0, d0, d0);
		List<?> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
		Iterator<?> iterator = list.iterator();

		while(iterator.hasNext())
			addPlayerName(((EntityPlayer)iterator.next()).getCommandSenderName());

		sendChangeToClient();
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
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);

		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				par1NBTTagCompound.setString("player" + i, players[i]);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);

		for(int i = 0; i < players.length; i++)
			if (par1NBTTagCompound.hasKey("player" + i))
				players[i] = par1NBTTagCompound.getString("player" + i);
	}

	public void sendChangeToClient(){
		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				//TODO
				mod_SecurityCraft.network.sendToAll(new PacketUpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, players[i]));
	}

}
