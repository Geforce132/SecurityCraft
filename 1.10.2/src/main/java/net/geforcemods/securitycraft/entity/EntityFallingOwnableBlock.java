package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityFallingOwnableBlock extends EntityFallingBlock
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(EntityFallingOwnableBlock.class, Owner.SERIALIZER);

	public EntityFallingOwnableBlock(World world)
	{
		super(world);
	}

	public EntityFallingOwnableBlock(World world, double x, double y, double z, IBlockState fallingBlockState, Owner owner)
	{
		super(world, x, y, z, fallingBlockState);

		dataManager.set(OWNER, owner);
		tileEntityData = getOwnerTag();
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();

		dataManager.register(OWNER, new Owner());
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
		Owner owner = dataManager.get(OWNER);

		tag.setString("owner", owner.getName());
		tag.setString("ownerUUID", owner.getUUID());
		return tag;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag)
	{
		NBTTagCompound teTag = tag.getCompoundTag("TileEntityData");
		String name = teTag.getString("owner");
		String uuid = teTag.getString("ownerUUID");

		dataManager.set(OWNER, new Owner(name, uuid));

		super.readEntityFromNBT(tag);
	}
}
