package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;

public class BlockLaserBlock extends BlockOwnable {

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockLaserBlock(Material material) {
		super(SoundType.METAL, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public boolean isNormalCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, entity, stack);

		if(!world.isRemote)
			setLaser(world, pos);
	}

	public void setLaser(World world, BlockPos pos)
	{
		for(EnumFacing facing : EnumFacing.values())
		{
			int boundType = facing == EnumFacing.UP || facing == EnumFacing.DOWN ? 1 : (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? 2 : 3);

			inner: for(int i = 1; i <= ServerConfig.CONFIG.laserBlockRange.get(); i++)
			{
				BlockPos offsetPos = pos.offset(facing, i);
				Block id = world.getBlockState(offsetPos).getBlock();

				if(id != Blocks.AIR && id != SCContent.laserBlock)
					break inner;
				else if(id == SCContent.laserBlock)
				{
					CustomizableSCTE.link((CustomizableSCTE)world.getTileEntity(pos), (CustomizableSCTE)world.getTileEntity(offsetPos));

					for(int j = 1; j < i; j++)
					{
						offsetPos = pos.offset(facing, j);

						if(world.getBlockState(offsetPos).getBlock() == Blocks.AIR)
							world.setBlockState(offsetPos, SCContent.laserField.getDefaultState().with(BlockLaserField.BOUNDTYPE, boundType));
					}
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state) {
		if(!world.isRemote)
			destroyAdjacentLasers(world, pos);
	}

	public static void destroyAdjacentLasers(World world, BlockPos pos)
	{
		for(EnumFacing facing : EnumFacing.values())
		{
			int boundType = facing == EnumFacing.UP || facing == EnumFacing.DOWN ? 1 : (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? 2 : 3);

			for(int i = 1; i <= ServerConfig.CONFIG.laserBlockRange.get(); i++)
			{
				BlockPos offsetPos = pos.offset(facing, i);

				if(world.getBlockState(offsetPos).getBlock() == SCContent.laserBlock)
				{
					for(int j = 1; j < i; j++)
					{
						offsetPos = pos.offset(facing, j);

						IBlockState state = world.getBlockState(offsetPos);

						if(state.getBlock() == SCContent.laserField && state.get(BlockLaserField.BOUNDTYPE) == boundType)
							world.destroyBlock(offsetPos, false);
					}
				}
			}
		}
	}

	@Override
	public boolean canProvidePower(IBlockState state){
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void tick(IBlockState state, World world, BlockPos pos, Random random){
		if (!world.isRemote && state.get(POWERED))
			BlockUtils.setBlockProperty(world, pos, POWERED, false, true);
	}

	@Override
	public void randomTick(IBlockState state, World world, BlockPos pos, Random rand){
		if((state.get(POWERED))){
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float f1 = 0.6F + 0.4F;
			float f2 = Math.max(0.0F, 0.7F - 0.5F);
			float f3 = Math.max(0.0F, 0.6F - 0.7F);

			world.addParticle(new RedstoneParticleData(1, f1, f2, f3), x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(1, f1, f2, f3), x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(1, f1, f2, f3), x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(1, f1, f2, f3), x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(new RedstoneParticleData(1, f1, f2, f3), x, y, z, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(POWERED, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (state.getValue(POWERED).booleanValue() ? 1 : 0);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityLaserBlock().linkable();
	}

}
