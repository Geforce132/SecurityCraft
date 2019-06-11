package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityFallingOwnableBlock extends FallingBlockEntity
{
	private static final DataParameter<Owner> OWNER = EntityDataManager.<Owner>createKey(EntityFallingOwnableBlock.class, Owner.SERIALIZER);

	public EntityFallingOwnableBlock(World world)
	{
		super(EntityType.FALLING_BLOCK, world);
	}

	public EntityFallingOwnableBlock(World world, double x, double y, double z, BlockState fallingBlockState, Owner owner)
	{
		super(world, x, y, z, fallingBlockState);

		dataManager.set(OWNER, owner);
		tileEntityData = getOwnerTag();
	}

	@Override
	protected void registerData()
	{
		super.registerData();

		dataManager.register(OWNER, new Owner());
	}

	@Override
	protected void writeAdditional(CompoundNBT tag)
	{
		tag.put("TileEntityData", getOwnerTag());
		super.writeAdditional(tag);
	}

	public CompoundNBT getOwnerTag()
	{
		CompoundNBT tag = new CompoundNBT();
		Owner owner = dataManager.get(OWNER);

		tag.putString("owner", owner.getName());
		tag.putString("ownerUUID", owner.getUUID());
		return tag;
	}

	@Override
	protected void readAdditional(CompoundNBT tag)
	{
		CompoundNBT teTag = tag.getCompound("TileEntityData");
		String name = teTag.getString("owner");
		String uuid = teTag.getString("ownerUUID");

		dataManager.set(OWNER, new Owner(name, uuid));

		super.readAdditional(tag);
	}
}
