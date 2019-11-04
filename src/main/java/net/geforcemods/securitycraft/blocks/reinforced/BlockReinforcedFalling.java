package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.entity.EntityFallingOwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedFalling extends BlockReinforcedBase
{
	public static boolean fallInstantly;

	public BlockReinforcedFalling(Material material, Block disguisedBlock)
	{
		super(material, 1, disguisedBlock);

		if(material == Material.SAND)
			setSoundType(SoundType.SAND);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
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

					world.setBlockToAir(pos);

					for(blockpos = pos.down(); (world.isAirBlock(blockpos) || canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

					if(blockpos.getY() > 0)
						world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
				}
			}
		}
	}

	@Override
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

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		if(rand.nextInt(16) == 0)
		{
			if(canFallThrough(world.getBlockState(pos.down())))
			{
				double particleX = pos.getX() + rand.nextFloat();
				double particleY = pos.getY() - 0.05D;
				double particleZ = pos.getZ() + rand.nextFloat();

				world.spawnParticle(EnumParticleTypes.FALLING_DUST, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D, Block.getStateId(state));
			}
		}
	}
}
