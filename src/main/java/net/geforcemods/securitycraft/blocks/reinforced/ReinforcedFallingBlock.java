package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
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

public class ReinforcedFallingBlock extends BaseReinforcedBlock
{
	public ReinforcedFallingBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag)
	{
		world.getBlockTicks().scheduleTick(pos, this, 2);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		world.getBlockTicks().scheduleTick(currentPos, this, 2);
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if(!world.isClientSide)
			checkFallable(world, pos);
	}

	private void checkFallable(World world, BlockPos pos)
	{
		if(canFallThrough(world.getBlockState(pos.below())) && pos.getY() >= 0)
		{
			if(world.hasChunksAt(pos.offset(-32, -32, -32), pos.offset(32, 32, 32)))
			{
				TileEntity te = world.getBlockEntity(pos);

				if(!world.isClientSide && te instanceof IOwnable)
				{
					FallingBlockEntity entity = new FallingBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos));

					entity.blockData = te.save(new CompoundNBT());
					world.addFreshEntity(entity);
				}
			}
			else
			{
				BlockState state = defaultBlockState();

				if(world.getBlockState(pos).getBlock() == this)
				{
					state = world.getBlockState(pos);
					world.destroyBlock(pos, false);
				}

				BlockPos blockpos;

				for(blockpos = pos.below(); canFallThrough(world.getBlockState(blockpos)) && blockpos.getY() > 0; blockpos = blockpos.below()) {}

				if(blockpos.getY() > 0)
					world.setBlockAndUpdate(blockpos.above(), state); //Forge: Fix loss of state information during world gen.
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