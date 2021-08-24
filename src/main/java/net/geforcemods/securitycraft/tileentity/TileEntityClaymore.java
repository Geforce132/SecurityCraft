package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.mines.BlockClaymore;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityClaymore extends CustomizableSCTE
{
	private OptionInt range = new OptionInt(this::getPos, "range", 5, 1, 10, 1, true);
	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;

	@Override
	public void update() {
		if(!world.isRemote)
		{
			IBlockState state = world.getBlockState(pos);

			if(state.getValue(BlockClaymore.DEACTIVATED))
				return;

			if(cooldown > 0){
				cooldown--;
				return;
			}

			if(cooldown == 0){
				((BlockClaymore)getBlockType()).explode(world, pos);
				return;
			}

			EnumFacing dir = state.getValue(BlockClaymore.FACING);
			AxisAlignedBB area = BlockUtils.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

			if(dir == EnumFacing.NORTH)
				area = area.contract(-0, -0, range.get());
			else if(dir == EnumFacing.SOUTH)
				area = area.contract(-0, -0, -range.get());
			else if(dir == EnumFacing.EAST)
				area = area.contract(-range.get(), -0, -0);
			else if(dir == EnumFacing.WEST)
				area = area.contract(range.get(), -0, -0);

			List<?> entities = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, area, e -> !EntityUtils.isInvisible(e));
			Iterator<?> iterator = entities.iterator();
			EntityLivingBase entity;

			while(iterator.hasNext()){
				entity = (EntityLivingBase) iterator.next();

				if(PlayerUtils.isPlayerMountedOnCamera(entity) || EntityUtils.doesEntityOwn(entity, world, pos))
					continue;

				entityX = entity.posX;
				entityY = entity.posY;
				entityZ = entity.posZ;
				cooldown = 20;
				getWorld().playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);
				break;
			}
		}

	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		writeOptions(tag);
		tag.setInteger("cooldown", cooldown);
		tag.setDouble("entityX", entityX);
		tag.setDouble("entityY", entityY);
		tag.setDouble("entityZ", entityZ);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		readOptions(tag);
		cooldown = tag.getInteger("cooldown");
		entityX = tag.getDouble("entityX");
		entityY = tag.getDouble("entityY");
		entityZ = tag.getDouble("entityZ");
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{range};
	}

	@Override
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[0];
	}
}
