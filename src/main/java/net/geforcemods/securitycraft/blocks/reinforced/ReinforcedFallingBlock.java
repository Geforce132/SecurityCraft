package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ReinforcedFallingBlock extends BaseReinforcedBlock {
	public ReinforcedFallingBlock(AbstractBlock.Properties properties, Block vB) {
		super(properties, vB);
	}

	@Override
	public void onPlace(BlockState state, World level, BlockPos pos, BlockState oldState, boolean flag) {
		level.getBlockTicks().scheduleTick(pos, this, 2);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		level.getBlockTicks().scheduleTick(currentPos, this, 2);
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (!level.isClientSide)
			checkFallable(level, pos);
	}

	private void checkFallable(World level, BlockPos pos) {
		if (canFallThrough(level.getBlockState(pos.below())) && pos.getY() >= 0) {
			if (level.hasChunksAt(pos.offset(-32, -32, -32), pos.offset(32, 32, 32))) {
				TileEntity be = level.getBlockEntity(pos);

				if (!level.isClientSide && be instanceof IOwnable) {
					FallingBlockEntity entity = new FallingBlockEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getBlockState(pos));

					entity.blockData = be.save(new CompoundNBT());
					level.addFreshEntity(entity);
				}
			}
			else {
				BlockState state = defaultBlockState();

				if (level.getBlockState(pos).getBlock() == this) {
					state = level.getBlockState(pos);
					level.destroyBlock(pos, false);
				}

				BlockPos blockpos;

				for (blockpos = pos.below(); canFallThrough(level.getBlockState(blockpos)) && blockpos.getY() > 0; blockpos = blockpos.below()) {}

				if (blockpos.getY() > 0)
					level.setBlockAndUpdate(blockpos.above(), state); //Forge: Fix loss of state information during world gen.
			}
		}
	}

	public static boolean canFallThrough(BlockState state) {
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return state.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
	}
}