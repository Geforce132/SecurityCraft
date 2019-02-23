package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.network.packets.PacketCRequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityType;

public class TileEntitySecretSign extends TileEntitySign implements IOwnable
{
	private Owner owner = new Owner();

	@Override
	public TileEntityType<?> getType()
	{
		return SCContent.teTypeSecretSign;
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound write(NBTTagCompound tag)
	{
		super.write(tag);

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
	public void read(NBTTagCompound tag)
	{
		super.read(tag);

		if (tag.contains("owner", NBTUtils.STRING))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.contains("ownerUUID", NBTUtils.STRING))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		write(tag);
		return new SPacketUpdateTileEntity(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		read(packet.getNbtCompound());
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
