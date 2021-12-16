package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class ReinforcedFallingBlock extends BaseReinforcedBlock
{
	public ReinforcedFallingBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean flag)
	{
		level.getBlockTicks().scheduleTick(pos, this, 2);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
	{
		level.getBlockTicks().scheduleTick(currentPos, this, 2);
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random)
	{
		if(!level.isClientSide)
			checkFallable(level, pos);
	}

	private void checkFallable(Level level, BlockPos pos)
	{
		//TODO: check 1.18
		if(canFallThrough(level.getBlockState(pos.below())) && pos.getY() >= 0)
		{
			if(level.hasChunksAt(pos.offset(-32, -32, -32), pos.offset(32, 32, 32)))
			{
				BlockEntity be = level.getBlockEntity(pos);

				if(!level.isClientSide && be instanceof IOwnable)
				{
					FallingBlockEntity entity = new FallingBlockEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.getBlockState(pos));

					entity.blockData = be.save(new CompoundTag());
					level.addFreshEntity(entity);
				}
			}
			else
			{
				BlockState state = defaultBlockState();

				if(level.getBlockState(pos).getBlock() == this)
				{
					state = level.getBlockState(pos);
					level.destroyBlock(pos, false);
				}

				BlockPos landedPos;

				//TODO: 1.18 check this
				for(landedPos = pos.below(); canFallThrough(level.getBlockState(landedPos)) && landedPos.getY() > 0; landedPos = landedPos.below()) {}

				if(landedPos.getY() > 0)
					level.setBlockAndUpdate(landedPos.above(), state); //Forge: Fix loss of state information during world gen.
			}
		}
	}

	public static boolean canFallThrough(BlockState state)
	{
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return state.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
	}
}