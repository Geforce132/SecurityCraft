package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRedstoneOreMine extends BlockFullMineBase
{
	public static final PropertyBool LIT = PropertyBool.create("lit");

	public BlockRedstoneOreMine()
	{
		super(Material.ROCK, Blocks.REDSTONE_ORE);

		setTickRandomly(true);
		setDefaultState(getDefaultState().withProperty(LIT, false));
	}

	@Override
	public int tickRate(World world)
	{
		return 30;
	}

	@Override
	public int getLightValue(IBlockState state)
	{
		return state.getValue(LIT) ? 9 : 0;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
	{
		activate(world, pos);
		super.onBlockClicked(world, pos, player);
	}

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity)
	{
		activate(world, pos);
		super.onEntityWalk(world, pos, entity);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		activate(world, pos);
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}

	private void activate(World world, BlockPos pos)
	{
		spawnParticles(world, pos);

		if(!world.getBlockState(pos).getValue(LIT))
			world.setBlockState(pos, getDefaultState().withProperty(LIT, true));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(state.getValue(LIT))
			world.setBlockState(pos, getDefaultState().withProperty(LIT, false));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		if(state.getValue(LIT))
			spawnParticles(world, pos);
	}

	private void spawnParticles(World world, BlockPos pos)
	{
		Random random = world.rand;

		for(int i = 0; i < 6; ++i)
		{
			double x = pos.getX() + random.nextFloat();
			double y = pos.getY() + random.nextFloat();
			double z = pos.getZ() + random.nextFloat();

			if(i == 0 && !world.getBlockState(pos.up()).isOpaqueCube())
				y = pos.getY() + 0.0625D + 1.0D;

			if(i == 1 && !world.getBlockState(pos.down()).isOpaqueCube())
				y = pos.getY() - 0.0625D;

			if(i == 2 && !world.getBlockState(pos.south()).isOpaqueCube())
				z = pos.getZ() + 0.0625D + 1.0D;

			if(i == 3 && !world.getBlockState(pos.north()).isOpaqueCube())
				z = pos.getZ() - 0.0625D;

			if(i == 4 && !world.getBlockState(pos.east()).isOpaqueCube())
				x = pos.getX() + 0.0625D + 1.0D;

			if(i == 5 && !world.getBlockState(pos.west()).isOpaqueCube())
				x = pos.getX() - 0.0625D;

			if(x < pos.getX() || x > pos.getX() + 1 || y < 0.0D || y > pos.getY() + 1 || z < pos.getZ() || z > pos.getZ() + 1)
				world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(LIT, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(LIT) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, LIT);
	}
}
