package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLaserBlock extends BlockOwnable {

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockLaserBlock(Material par2Material) {
		super(par2Material);
		setSoundType(SoundType.METAL);
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
	 * Called whenever the block is added into the world. Args: world, pos
	 */
	@Override
	public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(par1World, pos, state);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
		super.onBlockPlacedBy(par1World, pos, state, par5EntityLivingBase, par6ItemStack);

		if(!par1World.isRemote)
			setLaser(par1World, pos);
	}

	public void setLaser(World par1World, BlockPos pos) {
		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.east(i)).getBlock();
			if(id != Blocks.AIR && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) par1World.getTileEntity(pos), (CustomizableSCTE) par1World.getTileEntity(pos.east(i)));

				for(int j = 1; j < i; j++)
					if(par1World.getBlockState(pos.east(j)).getBlock() == Blocks.AIR)
						par1World.setBlockState(pos.east(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 3));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = par1World.getBlockState(new BlockPos(pos.west(i))).getBlock();
			if(id != Blocks.AIR && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) par1World.getTileEntity(pos), (CustomizableSCTE) par1World.getTileEntity(pos.west(i)));

				for(int j = 1; j < i; j++)
					if(par1World.getBlockState(pos.west(j)).getBlock() == Blocks.AIR)
						par1World.setBlockState(pos.west(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 3));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.south(i)).getBlock();
			if(id != Blocks.AIR && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) par1World.getTileEntity(pos), (CustomizableSCTE) par1World.getTileEntity(pos.south(i)));

				for(int j = 1; j < i; j++)
					if(par1World.getBlockState(pos.south(j)).getBlock() == Blocks.AIR)
						par1World.setBlockState(pos.south(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 2));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.north(i)).getBlock();
			if(id != Blocks.AIR && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) par1World.getTileEntity(pos), (CustomizableSCTE) par1World.getTileEntity(pos.north(i)));

				for(int j = 1; j < i; j++)
					if(par1World.getBlockState(pos.north(j)).getBlock() == Blocks.AIR)
						par1World.setBlockState(pos.north(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 2));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.up(i)).getBlock();
			if(id != Blocks.AIR && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) par1World.getTileEntity(pos), (CustomizableSCTE) par1World.getTileEntity(pos.up(i)));

				for(int j = 1; j < i; j++)
					if(par1World.getBlockState(pos.up(j)).getBlock() == Blocks.AIR)
						par1World.setBlockState(pos.up(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 1));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = par1World.getBlockState(pos.down(i)).getBlock();
			if(id != Blocks.AIR && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) par1World.getTileEntity(pos), (CustomizableSCTE) par1World.getTileEntity(pos.down(i)));

				for(int j = 1; j < i; j++)
					if(par1World.getBlockState(pos.down(j)).getBlock() == Blocks.AIR)
						par1World.setBlockState(pos.down(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 1));
			}
			else
				continue;
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onPlayerDestroy(World par1World, BlockPos pos, IBlockState state) {
		if(!par1World.isRemote)
			destroyAdjacentLasers(par1World, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void destroyAdjacentLasers(World par1World, int par2, int par3, int par4){
		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2 + i, par3, par4);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(par1World, par2 + j, par3, par4) == SCContent.laserField && par1World.getBlockState(new BlockPos(par2 + j, par3, par4)).getValue(BlockLaserField.BOUNDTYPE) == 3)
						par1World.destroyBlock(new BlockPos(par2 + j, par3, par4), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2 - i, par3, par4);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(par1World, par2 - j, par3, par4) == SCContent.laserField && par1World.getBlockState(new BlockPos(par2 - j, par3, par4)).getValue(BlockLaserField.BOUNDTYPE) == 3)
						par1World.destroyBlock(new BlockPos(par2 - j, par3, par4), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2, par3, par4 + i);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(par1World, par2, par3, par4 + j) == SCContent.laserField && par1World.getBlockState(new BlockPos(par2, par3, par4 + j)).getValue(BlockLaserField.BOUNDTYPE) == 2)
						par1World.destroyBlock(new BlockPos(par2, par3, par4 + j), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2 , par3, par4 - i);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(par1World, par2, par3, par4 - j) == SCContent.laserField && par1World.getBlockState(new BlockPos(par2, par3, par4 - j)).getValue(BlockLaserField.BOUNDTYPE) == 2)
						par1World.destroyBlock(new BlockPos(par2, par3, par4 - j), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2, par3 + i, par4);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(par1World, par2, par3 + j, par4) == SCContent.laserField && par1World.getBlockState(new BlockPos(par2, par3 + j, par4)).getValue(BlockLaserField.BOUNDTYPE) == 1)
						par1World.destroyBlock(new BlockPos(par2, par3 + j, par4), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(par1World, par2, par3 - i, par4);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(par1World, par2, par3 - j, par4) == SCContent.laserField && par1World.getBlockState(new BlockPos(par2, par3 + j, par4)).getValue(BlockLaserField.BOUNDTYPE) == 1)
						par1World.destroyBlock(new BlockPos(par2, par3 - j, par4), false);
			}
			else
				continue;
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
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.getValue(POWERED).booleanValue())
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.getValue(POWERED).booleanValue())
			return 15;
		else
			return 0;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random){
		if (!par1World.isRemote && state.getValue(POWERED).booleanValue())
			BlockUtils.setBlockProperty(par1World, pos, POWERED, false, true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand){
		if(stateIn.getValue(POWERED).booleanValue()){
			double d0 = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double d1 = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double d2 = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;


			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
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
	public TileEntity createNewTileEntity(World par1World, int par2) {
		return new TileEntityLaserBlock().linkable();
	}

}
