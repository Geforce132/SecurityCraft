package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLaserBlock extends BlockOwnable {

	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockLaserBlock(Material material) {
		super(material);
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, BlockPos pos){
		return true;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, pos
	 */
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(world, pos, state);
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

	public void setLaser(World world, BlockPos pos) {
		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlockState(pos.east(i)).getBlock();
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(pos), (CustomizableSCTE) world.getTileEntity(pos.east(i)));

				for(int j = 1; j < i; j++)
					if(world.getBlockState(pos.east(j)).getBlock() == Blocks.air)
						world.setBlockState(pos.east(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 3));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlockState(new BlockPos(pos.west(i))).getBlock();
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(pos), (CustomizableSCTE) world.getTileEntity(pos.west(i)));

				for(int j = 1; j < i; j++)
					if(world.getBlockState(pos.west(j)).getBlock() == Blocks.air)
						world.setBlockState(pos.west(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 3));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlockState(pos.south(i)).getBlock();
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(pos), (CustomizableSCTE) world.getTileEntity(pos.south(i)));

				for(int j = 1; j < i; j++)
					if(world.getBlockState(pos.south(j)).getBlock() == Blocks.air)
						world.setBlockState(pos.south(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 2));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlockState(pos.north(i)).getBlock();
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(pos), (CustomizableSCTE) world.getTileEntity(pos.north(i)));

				for(int j = 1; j < i; j++)
					if(world.getBlockState(pos.north(j)).getBlock() == Blocks.air)
						world.setBlockState(pos.north(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 2));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlockState(pos.up(i)).getBlock();
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(pos), (CustomizableSCTE) world.getTileEntity(pos.up(i)));

				for(int j = 1; j < i; j++)
					if(world.getBlockState(pos.up(j)).getBlock() == Blocks.air)
						world.setBlockState(pos.up(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 1));
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = world.getBlockState(pos.down(i)).getBlock();
			if(id != Blocks.air && id != SCContent.laserBlock)
				break;
			if(id == SCContent.laserBlock){
				CustomizableSCTE.link((CustomizableSCTE) world.getTileEntity(pos), (CustomizableSCTE) world.getTileEntity(pos.down(i)));

				for(int j = 1; j < i; j++)
					if(world.getBlockState(pos.down(j)).getBlock() == Blocks.air)
						world.setBlockState(pos.down(j), SCContent.laserField.getDefaultState().withProperty(BlockLaserField.BOUNDTYPE, 1));
			}
			else
				continue;
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
	 */
	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		if(!world.isRemote)
			destroyAdjacentLasers(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void destroyAdjacentLasers(World world, int x, int y, int z){
		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(world, x + i, y, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(world, x + j, y, z) == SCContent.laserField && world.getBlockState(new BlockPos(x + j, y, z)).getValue(BlockLaserField.BOUNDTYPE) == 3)
						world.destroyBlock(new BlockPos(x + j, y, z), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(world, x - i, y, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(world, x - j, y, z) == SCContent.laserField && world.getBlockState(new BlockPos(x - j, y, z)).getValue(BlockLaserField.BOUNDTYPE) == 3)
						world.destroyBlock(new BlockPos(x - j, y, z), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(world, x, y, z + i);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(world, x, y, z + j) == SCContent.laserField && world.getBlockState(new BlockPos(x, y, z + j)).getValue(BlockLaserField.BOUNDTYPE) == 2)
						world.destroyBlock(new BlockPos(x, y, z + j), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(world, x , y, z - i);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(world, x, y, z - j) == SCContent.laserField && world.getBlockState(new BlockPos(x, y, z - j)).getValue(BlockLaserField.BOUNDTYPE) == 2)
						world.destroyBlock(new BlockPos(x, y, z - j), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(world, x, y + i, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(world, x, y + j, z) == SCContent.laserField && world.getBlockState(new BlockPos(x, y + j, z)).getValue(BlockLaserField.BOUNDTYPE) == 1)
						world.destroyBlock(new BlockPos(x, y + j, z), false);
			}
			else
				continue;
		}

		for(int i = 1; i <= SecurityCraft.config.laserBlockRange; i++){
			Block id = BlockUtils.getBlock(world, x, y - i, z);
			if(id == SCContent.laserBlock){
				for(int j = 1; j < i; j++)
					if(BlockUtils.getBlock(world, x, y - j, z) == SCContent.laserField && world.getBlockState(new BlockPos(x, y + j, z)).getValue(BlockLaserField.BOUNDTYPE) == 1)
						world.destroyBlock(new BlockPos(x, y - j, z), false);
			}
			else
				continue;
		}
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side){
		if(state.getValue(POWERED).booleanValue())
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side){
		if(state.getValue(POWERED).booleanValue())
			return 15;
		else
			return 0;
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random){
		if (!world.isRemote && state.getValue(POWERED).booleanValue())
			BlockUtils.setBlockProperty(world, pos, POWERED, false, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random random){
		if(state.getValue(POWERED).booleanValue()){
			double x = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (random.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;


			world.spawnParticle(EnumParticleTypes.REDSTONE, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
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
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {POWERED});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLaserBlock().linkable();
	}

}
