package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntityFallingOwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockFullMineFalling extends BlockFullMineBase
{
	public static boolean fallInstantly;

	public BlockFullMineFalling(Material material, Block disguisedBlock, float baseHardness)
	{
		super(material, disguisedBlock, baseHardness);
	}

	@Override
	public void onBlockAdded(IBlockState state, World world, BlockPos pos, IBlockState oldState)
	{
		world.getPendingBlockTicks().scheduleTick(pos, this, tickRate(world));
	}

	@Override
	public IBlockState updatePostPlacement(IBlockState state, EnumFacing facing, IBlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		world.getPendingBlockTicks().scheduleTick(currentPos, this, tickRate(world));
		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public void tick(IBlockState state, World world, BlockPos pos, Random random)
	{
		if(!world.isRemote)
		{
			if((world.isAirBlock(pos.down()) || canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0)
			{
				if(!fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
				{
					if(!world.isRemote && world.getTileEntity(pos) instanceof IOwnable)
						world.spawnEntity(new EntityFallingOwnableBlock(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos), ((IOwnable)world.getTileEntity(pos)).getOwner()));
				}
				else
				{
					BlockPos blockpos;

					world.removeBlock(pos);

					for(blockpos = pos.down(); (world.isAirBlock(blockpos) || canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

					if(blockpos.getY() > 0)
						world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
				}
			}
		}
	}

	/**
	 * How many world ticks before ticking
	 */
	public int tickRate(World world)
	{
		return 2;
	}

	public static boolean canFallThrough(IBlockState state)
	{
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
	}

	/**
	 * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
	 * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
	 * of whether the block can receive random update ticks
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		if(rand.nextInt(16) == 0)
		{
			if(canFallThrough(world.getBlockState(pos.down())))
			{
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() - 0.05D;
				double particleZ = pos.getZ() + rand.nextFloat();

				world.addParticle(new BlockParticleData(Particles.FALLING_DUST, state), false, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
