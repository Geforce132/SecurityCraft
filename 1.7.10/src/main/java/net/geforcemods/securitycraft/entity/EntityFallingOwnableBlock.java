package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityFallingOwnableBlock extends EntityFallingBlock
{
	public EntityFallingOwnableBlock(World world)
	{
		super(world);
	}

	public EntityFallingOwnableBlock(World world, double x, double y, double z, Block fallingBlock, int meta, Owner owner)
	{
		super(world, x, y, z, fallingBlock, meta);

		dataWatcher.updateObject(20, owner.getName());
		dataWatcher.updateObject(21, owner.getUUID());
		tileEntityData = getOwnerTag();
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataWatcher.addObject(20, "owner");
		dataWatcher.addObject(21, "ownerUUID");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag)
	{
		tag.setTag("TileEntityData", getOwnerTag());
		super.writeEntityToNBT(tag);
	}

	public NBTTagCompound getOwnerTag()
	{
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("owner", dataWatcher.getWatchableObjectString(20));
		tag.setString("ownerUUID", dataWatcher.getWatchableObjectString(21));
		return tag;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound teTag = tag.getCompoundTag("TileEntityData");
		String name = teTag.getString("owner");
		String uuid = teTag.getString("ownerUUID");

		dataWatcher.updateObject(20, name);
		dataWatcher.updateObject(21, uuid);

		super.readEntityFromNBT(tag);
	}
}
