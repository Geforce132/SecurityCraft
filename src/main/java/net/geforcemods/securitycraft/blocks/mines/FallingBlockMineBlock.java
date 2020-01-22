package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
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

public class FallingBlockMineBlock extends BaseFullMineBlock
{
	public static boolean fallInstantly;

	public FallingBlockMineBlock(Material material, SoundType soundType, Block disguisedBlock, float baseHardness)
	{
		super(material, soundType, disguisedBlock, baseHardness);
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
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if(!world.isRemote)
		{
			if((world.isAirBlock(pos.down()) || canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0)
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
					BlockPos blockpos;

					world.destroyBlock(pos, false);

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

	public static boolean canFallThrough(BlockState state)
	{
		Block block = state.getBlock();
		Material material = state.getMaterial();

		return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
	}

	/**
	 * Called periodically clientside on blocks near the player to show effects (like furnace fire ParticleTypes). Note that
	 * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
	 * of whether the block can receive random update ticks
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
	{
		if(rand.nextInt(16) == 0)
		{
			if(canFallThrough(world.getBlockState(pos.down())))
			{
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() - 0.05D;
				double particleZ = pos.getZ() + rand.nextFloat();

				world.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, state), false, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
