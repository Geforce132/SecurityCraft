package net.geforcemods.securitycraft.blockentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class ClaymoreBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity {
	private IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 10, 1, true);
	private int cooldown = -1;

	public ClaymoreBlockEntity() {
		super(SCContent.beTypeClaymore);
	}

	@Override
	public void tick() {
		if (!getLevel().isClientSide) {
			if (getBlockState().getValue(ClaymoreBlock.DEACTIVATED))
				return;

			if (cooldown > 0) {
				cooldown--;
				return;
			}

			if (cooldown == 0) {
				((ClaymoreBlock) getBlockState().getBlock()).explode(level, worldPosition);
				return;
			}

			Direction dir = getBlockState().getValue(ClaymoreBlock.FACING);
			AxisAlignedBB area = new AxisAlignedBB(worldPosition);

			if (dir == Direction.NORTH)
				area = area.contract(-0, -0, range.get());
			else if (dir == Direction.SOUTH)
				area = area.contract(-0, -0, -range.get());
			else if (dir == Direction.EAST)
				area = area.contract(-range.get(), -0, -0);
			else if (dir == Direction.WEST)
				area = area.contract(range.get(), -0, -0);

			getLevel().getEntitiesOfClass(LivingEntity.class, area, e -> !EntityUtils.isInvisible(e) && !e.isSpectator() && !EntityUtils.doesEntityOwn(e, level, worldPosition)).stream().findFirst().ifPresent(entity -> {
				cooldown = 20;
				getLevel().playSound(null, new BlockPos(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D), SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);
			});
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		writeOptions(tag);
		tag.putInt("cooldown", cooldown);
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		readOptions(tag);
		cooldown = tag.getInt("cooldown");
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[0];
	}
}
