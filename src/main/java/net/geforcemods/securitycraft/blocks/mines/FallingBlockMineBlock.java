package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FallingBlockMineBlock extends BaseFullMineBlock {
	public FallingBlockMineBlock(Block.Properties properties, Block disguisedBlock) {
		super(properties, disguisedBlock);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean flag) {
		level.getBlockTicks().scheduleTick(pos, this, 2);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		level.getBlockTicks().scheduleTick(currentPos, this, 2);
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		//TODO: 1.18 check this
		if ((level.isEmptyBlock(pos.below()) || canFallThrough(level.getBlockState(pos.below()))) && pos.getY() >= 0) {
			if (level.hasChunksAt(pos.offset(-32, -32, -32), pos.offset(32, 32, 32))) {
				BlockEntity be = level.getBlockEntity(pos);

				if (!level.isClientSide && be instanceof IOwnable) {
					FallingBlockEntity entity = new FallingBlockEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getBlockState(pos));

					entity.blockData = be.save(new CompoundTag());
					level.addFreshEntity(entity);
				}
			}
			else {
				BlockPos landedPos;

				level.destroyBlock(pos, false);

				//TODO: 1.18 check this
				for (landedPos = pos.below(); (level.isEmptyBlock(landedPos) || canFallThrough(level.getBlockState(landedPos))) && landedPos.getY() > 0; landedPos = landedPos.below()) {}

				if (landedPos.getY() > 0)
					level.setBlockAndUpdate(landedPos.above(), state); //Forge: Fix loss of state information during world gen.
			}
		}
	}

	public static boolean canFallThrough(BlockState state) {
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return block == Blocks.FIRE || state.isAir() || material == Material.WATER || material == Material.LAVA;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, Random rand) {
		if (rand.nextInt(16) == 0) {
			if (canFallThrough(level.getBlockState(pos.below()))) {
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() - 0.05D;
				double particleZ = pos.getZ() + rand.nextFloat();

				level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), false, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
