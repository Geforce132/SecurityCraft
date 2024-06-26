package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class KeypadFurnaceBlock extends AbstractKeypadFurnaceBlock {
	public KeypadFurnaceBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
		if (state.getValue(LIT) && IDisguisable.getDisguisedStateOrDefault(state, level, pos).getBlock() == this) {
			double x = pos.getX() + 0.5D;
			double y = pos.getY();
			double z = pos.getZ() + 0.5D;

			if (rand.nextDouble() < 0.1D)
				level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

			if (state.getValue(OPEN)) {
				Direction direction = state.getValue(FACING);
				Axis axis = direction.getAxis();
				double randomNumber = rand.nextDouble() * 0.6D - 0.3D;
				double xOffset = axis == Axis.X ? direction.getStepX() * 0.32D : randomNumber;
				double yOffset = rand.nextDouble() * 6.0D / 16.0D;
				double zOffset = axis == Axis.Z ? direction.getStepZ() * 0.32D : randomNumber;

				level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
				level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new KeypadFurnaceBlockEntity();
	}
}
