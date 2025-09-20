package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlastFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class KeypadBlastFurnaceBlock extends AbstractKeypadFurnaceBlock {
	public KeypadBlastFurnaceBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT) && IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state).getBlock() == this) {
			double x = pos.getX() + 0.5D;
			double y = pos.getY();
			double z = pos.getZ() + 0.5D;

			if (random.nextDouble() < 0.1D)
				level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);

			if (state.getValue(OPEN)) {
				Direction direction = state.getValue(FACING);
				Direction.Axis axis = direction.getAxis();
				double randomNumber = random.nextDouble() * 0.6D - 0.3D;
				double xOffset = axis == Direction.Axis.X ? direction.getStepX() * 0.32D : randomNumber;
				double yOffset = random.nextDouble() * 9.0D / 16.0D;
				double zOffset = axis == Direction.Axis.Z ? direction.getStepZ() * 0.32D : randomNumber;

				level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, SCContent.KEYPAD_BLAST_FURNACE_BLOCK_ENTITY.get(), AbstractKeypadFurnaceBlockEntity::serverTick);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeypadBlastFurnaceBlockEntity(pos, state);
	}
}
