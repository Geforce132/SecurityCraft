package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityOwnable extends TileEntitySCTE implements IOwnable {

	private Owner owner = new Owner();

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		if(owner != null){
			par1NBTTagCompound.setString("owner", owner.getName());
			par1NBTTagCompound.setString("ownerUUID", owner.getUUID());
		}
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("owner"))
			owner.setOwnerName(par1NBTTagCompound.getString("owner"));

		if (par1NBTTagCompound.hasKey("ownerUUID"))
			owner.setOwnerUUID(par1NBTTagCompound.getString("ownerUUID"));
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

}
