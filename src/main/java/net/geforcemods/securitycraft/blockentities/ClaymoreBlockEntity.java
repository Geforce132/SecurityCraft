package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ClaymoreBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity
{
	private IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 10, 1, true);
	private int cooldown = -1;

	public ClaymoreBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeClaymore, pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) { //server only as per ClaymoreBlock
		if(state.getValue(ClaymoreBlock.DEACTIVATED))
			return;

		if(cooldown > 0){
			cooldown--;
			return;
		}

		if(cooldown == 0){
			((ClaymoreBlock)state.getBlock()).explode(level, pos);
			return;
		}

		Direction dir = state.getValue(ClaymoreBlock.FACING);
		AABB area = new AABB(pos);

		area = switch(dir) {
			case NORTH -> area.contract(0, 0, range.get());
			case SOUTH -> area.contract(0, 0, -range.get());
			case EAST -> area.contract(-range.get(), 0, 0);
			case WEST -> area.contract(range.get(), 0, 0);
			default -> area;
		};

		level.getEntitiesOfClass(LivingEntity.class, area, e -> !EntityUtils.isInvisible(e) && !e.isSpectator() && !EntityUtils.doesEntityOwn(e, level, pos))
		.stream().findFirst().ifPresent(entity -> {
			cooldown = 20;
			level.playSound(null, new BlockPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
		});
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);
		writeOptions(tag);
		tag.putInt("cooldown", cooldown);
		return tag;
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		readOptions(tag);
		cooldown = tag.getInt("cooldown");
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
