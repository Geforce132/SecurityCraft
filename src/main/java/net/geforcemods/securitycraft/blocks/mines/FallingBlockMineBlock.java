package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FallingBlockMineBlock extends BaseFullMineBlock {
	public FallingBlockMineBlock(AbstractBlock.Properties properties, Block disguisedBlock) {
		super(properties, disguisedBlock);
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
		if (!level.isClientSide && (level.isEmptyBlock(pos.below()) || canFallThrough(level.getBlockState(pos.below()))) && pos.getY() >= 0) {
			if (level.hasChunksAt(pos.offset(-32, -32, -32), pos.offset(32, 32, 32))) {
				TileEntity te = level.getBlockEntity(pos);

				if (!level.isClientSide && te instanceof IOwnable) {
					FallingBlockEntity entity = new FallingBlockEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getBlockState(pos));

					entity.blockData = te.save(new CompoundNBT());
					level.addFreshEntity(entity);
				}
			}
			else {
				BlockPos blockpos;

				level.destroyBlock(pos, false);

				for (blockpos = pos.below(); (level.isEmptyBlock(blockpos) || canFallThrough(level.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.below()) {}

				if (blockpos.getY() > 0)
					level.setBlockAndUpdate(blockpos.above(), state); //Forge: Fix loss of state information during world gen.
			}
		}
	}

	public static boolean canFallThrough(BlockState state) {
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
		if (rand.nextInt(16) == 0 && canFallThrough(level.getBlockState(pos.below()))) {
			double particleX = pos.getX() + rand.nextFloat();
			double particleY = pos.getY() - 0.05D;
			double particleZ = pos.getZ() + rand.nextFloat();

			level.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, state), false, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
		}
	}
}
