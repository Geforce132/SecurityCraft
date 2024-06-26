package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.KeypadSmokerBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class KeypadSmokerBlock extends AbstractKeypadFurnaceBlock {
	public KeypadSmokerBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
		if (rand.nextDouble() < 0.1D && state.getValue(LIT) && IDisguisable.getDisguisedStateOrDefault(state, level, pos).getBlock() == this)
			level.playLocalSound(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.SMOKER_SMOKE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new KeypadSmokerBlockEntity();
	}
}
