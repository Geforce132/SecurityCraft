package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.network.packets.PacketCRequestTEOwnableUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntitySign;

public class TileEntitySecretSign extends TileEntitySign implements IOwnable
{
	private Owner owner = new Owner();

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		if(owner != null){
			tag.setString("owner", owner.getName());
			tag.setString("ownerUUID", owner.getUUID());
		}

		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.hasKey("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	@Override
	public void onLoad()
	{
		if(world.isRemote)
			SecurityCraft.network.sendToServer(new PacketCRequestTEOwnableUpdate(getPos(), getWorld().provider.getDimension()));
	}
}
