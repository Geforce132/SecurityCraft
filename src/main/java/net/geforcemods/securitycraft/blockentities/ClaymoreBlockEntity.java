package net.geforcemods.securitycraft.blockentities;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ClaymoreBlockEntity extends CustomizableBlockEntity
{
	private IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 10, 1, true);
	private double entityX = -1D;
	private double entityY = -1D;
	private double entityZ = -1D;
	private int cooldown = -1;

	public ClaymoreBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeClaymore, pos, state);
	}

	@Override
	public void tick(Level world, BlockPos pos, BlockState state) {  //server only as per ClaymoreBlock
		if(state.getValue(ClaymoreBlock.DEACTIVATED))
			return;

		if(cooldown > 0){
			cooldown--;
			return;
		}

		if(cooldown == 0){
			((ClaymoreBlock)state.getBlock()).explode(world, pos);
			return;
		}

		Direction dir = state.getValue(ClaymoreBlock.FACING);
		AABB area = new AABB(pos);

		if(dir == Direction.NORTH)
			area = area.contract(-0, -0, range.get());
		else if(dir == Direction.SOUTH)
			area = area.contract(-0, -0, -range.get());
		else if(dir == Direction.EAST)
			area = area.contract(-range.get(), -0, -0);
		else if(dir == Direction.WEST)
			area = area.contract(range.get(), -0, -0);

		List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, area, e -> !EntityUtils.isInvisible(e));
		Iterator<LivingEntity> iterator = entities.iterator();
		LivingEntity entity;

		while(iterator.hasNext()){
			entity = iterator.next();

			if(EntityUtils.doesEntityOwn(entity, world, pos))
				continue;

			entityX = entity.getX();
			entityY = entity.getY();
			entityZ = entity.getZ();
			cooldown = 20;
			world.playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
			break;
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
	public void load(CompoundTag tag)
	{
		super.load(tag);

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
