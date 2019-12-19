package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ReinforcedFallingBlock extends BaseReinforcedBlock
{
	public static boolean fallInstantly;

	public ReinforcedFallingBlock(SoundType soundType, Material mat, Block vB, String registryPath)
	{
		super(soundType, mat, vB, registryPath);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag)
	{
		world.getPendingBlockTicks().scheduleTick(pos, this, tickRate(world));
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		world.getPendingBlockTicks().scheduleTick(currentPos, this, tickRate(world));
		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void func_225534_a_(BlockState state, ServerWorld world, BlockPos pos, Random random) //tick
	{
		if(!world.isRemote)
			checkFallable(world, pos);
	}

	private void checkFallable(World world, BlockPos pos)
	{
		if(canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= 0)
		{
			if(!fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
			{
				TileEntity te = world.getTileEntity(pos);

				if(!world.isRemote && te instanceof IOwnable)
				{
					FallingBlockEntity entity = new FallingBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos));

					entity.tileEntityData = te.write(new CompoundNBT());
					world.addEntity(entity);
				}
			}
			else
			{
				BlockState state = getDefaultState();

				if(world.getBlockState(pos).getBlock() == this)
				{
					state = world.getBlockState(pos);
					world.destroyBlock(pos, false);
				}

				BlockPos blockpos;

				for(blockpos = pos.down(); canFallThrough(world.getBlockState(blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

				if(blockpos.getY() > 0)
					world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
			}
		}
	}

	@Override
	public int tickRate(IWorldReader world)
	{
		return 2;
	}

	public static boolean canFallThrough(BlockState state)
	{
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return state.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
	}
}