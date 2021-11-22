package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class ClaymoreTileEntity extends CustomizableTileEntity implements ITickableTileEntity
{
	private IntOption range = new IntOption(this::getPos, "range", 5, 1, 10, 1, true);
	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;

	public ClaymoreTileEntity()
	{
		super(SCContent.teTypeClaymore);
	}

	@Override
	public void tick() {
		if(!getWorld().isRemote)
		{
			if(getBlockState().get(ClaymoreBlock.DEACTIVATED))
				return;

			if(cooldown > 0){
				cooldown--;
				return;
			}

			if(cooldown == 0){
				((ClaymoreBlock)getBlockState().getBlock()).explode(world, pos);
				return;
			}

			Direction dir = getBlockState().get(ClaymoreBlock.FACING);
			AxisAlignedBB area = new AxisAlignedBB(pos);

			if(dir == Direction.NORTH)
				area = area.contract(-0, -0, range.get());
			else if(dir == Direction.SOUTH)
				area = area.contract(-0, -0, -range.get());
			else if(dir == Direction.EAST)
				area = area.contract(-range.get(), -0, -0);
			else if(dir == Direction.WEST)
				area = area.contract(range.get(), -0, -0);

			List<?> entities = getWorld().getEntitiesWithinAABB(LivingEntity.class, area, e -> !EntityUtils.isInvisible(e));
			Iterator<?> iterator = entities.iterator();
			LivingEntity entity;

			while(iterator.hasNext()){
				entity = (LivingEntity) iterator.next();

				if(PlayerUtils.isPlayerMountedOnCamera(entity) || EntityUtils.doesEntityOwn(entity, world, pos))
					continue;

				entityX = entity.getPosX();
				entityY = entity.getPosY();
				entityZ = entity.getPosZ();
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
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		writeOptions(tag);
		tag.putInt("cooldown", cooldown);
		tag.putDouble("entityX", entityX);
		tag.putDouble("entityY", entityY);
		tag.putDouble("entityZ", entityZ);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		readOptions(tag);
		cooldown = tag.getInt("cooldown");
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
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[0];
	}
}
