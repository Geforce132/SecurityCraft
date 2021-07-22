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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;

public class ClaymoreTileEntity extends CustomizableTileEntity
{
	private IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 10, 1, true);
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
		if(!getLevel().isClientSide)
		{
			if(getBlockState().getValue(ClaymoreBlock.DEACTIVATED))
				return;

			if(cooldown > 0){
				cooldown--;
				return;
			}

			if(cooldown == 0){
				((ClaymoreBlock)getBlockState().getBlock()).explode(level, worldPosition);
				return;
			}

			Direction dir = getBlockState().getValue(ClaymoreBlock.FACING);
			AABB area = new AABB(worldPosition);

			if(dir == Direction.NORTH)
				area = area.contract(-0, -0, range.get());
			else if(dir == Direction.SOUTH)
				area = area.contract(-0, -0, -range.get());
			else if(dir == Direction.EAST)
				area = area.contract(-range.get(), -0, -0);
			else if(dir == Direction.WEST)
				area = area.contract(range.get(), -0, -0);

			List<?> entities = getLevel().getEntitiesOfClass(LivingEntity.class, area, e -> !EntityUtils.isInvisible(e));
			Iterator<?> iterator = entities.iterator();
			LivingEntity entity;

			while(iterator.hasNext()){
				entity = (LivingEntity) iterator.next();

				if(PlayerUtils.isPlayerMountedOnCamera(entity) || EntityUtils.doesEntityOwn(entity, level, worldPosition))
					continue;

				entityX = entity.getX();
				entityY = entity.getY();
				entityZ = entity.getZ();
				cooldown = 20;
				getLevel().playSound(null, new BlockPos(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
				break;
			}
		}

	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);
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
	public void load(BlockState state, CompoundTag tag)
	{
		super.load(state, tag);

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
