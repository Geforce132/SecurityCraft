package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.tileentity.KeypadSmokerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class KeypadSmokerBlock extends AbstractKeypadFurnaceBlock {
	public KeypadSmokerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (rand.nextDouble() < 0.1D && state.get(LIT) && getDisguisedStateOrDefault(state, world, pos).getBlock() == this)
			world.playSound(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KeypadSmokerTileEntity();
	}
}
