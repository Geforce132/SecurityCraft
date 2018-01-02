package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLaserField extends BlockContainer implements IIntersectable{

	public static final PropertyInteger BOUNDTYPE = PropertyInteger.create("boundtype", 1, 3);

	public BlockLaserField(Material material) {
		super(material);
		setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);

	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state)
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.TRANSLUCENT;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote && entity instanceof EntityLivingBase && !EntityUtils.doesMobHavePotionEffect((EntityLivingBase) entity, Potion.invisibility)){
			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(world, pos.east(i));
				if(id == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, pos.east(i), BlockLaserBlock.POWERED)){
					if(world.getTileEntity(pos.east(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.east(i))).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos.east(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getCommandSenderName().toLowerCase()))
						return;
					BlockUtils.setBlockProperty(world, pos.east(i), BlockLaserBlock.POWERED, true, true);
					world.notifyNeighborsOfStateChange(pos.east(i), SCContent.laserBlock);
					world.scheduleUpdate(pos.east(i), SCContent.laserBlock, 50);

					if(world.getTileEntity(pos.east(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.east(i))).hasModule(EnumCustomModules.HARMING))
						((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(world, pos.west(i));
				if(id == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, pos.west(i), BlockLaserBlock.POWERED)){
					if(world.getTileEntity(pos.west(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.west(i))).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos.west(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getCommandSenderName().toLowerCase()))
						return;
					BlockUtils.setBlockProperty(world, pos.west(i), BlockLaserBlock.POWERED, true, true);
					world.notifyNeighborsOfStateChange(pos.west(i), SCContent.laserBlock);
					world.scheduleUpdate(pos.west(i), SCContent.laserBlock, 50);

					if(world.getTileEntity(pos.west(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.west(i))).hasModule(EnumCustomModules.HARMING))
						((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(world, pos.south(i));
				if(id == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, pos.south(i), BlockLaserBlock.POWERED)){
					if(world.getTileEntity(pos.south(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.south(i))).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos.south(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getCommandSenderName().toLowerCase()))
						return;
					BlockUtils.setBlockProperty(world, pos.south(i), BlockLaserBlock.POWERED, true, true);
					world.notifyNeighborsOfStateChange(pos.south(i), SCContent.laserBlock);
					world.scheduleUpdate(pos.south(i), SCContent.laserBlock, 50);

					if(world.getTileEntity(pos.south(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.south(i))).hasModule(EnumCustomModules.HARMING))
						((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(world, pos.north(i));
				if(id == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, pos.north(i), BlockLaserBlock.POWERED)){
					if(world.getTileEntity(pos.north(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.north(i))).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos.north(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getCommandSenderName().toLowerCase()))
						return;
					BlockUtils.setBlockProperty(world, pos.north(i), BlockLaserBlock.POWERED, true, true);
					world.notifyNeighborsOfStateChange(pos.north(i), SCContent.laserBlock);
					world.scheduleUpdate(pos.north(i), SCContent.laserBlock, 50);

					if(world.getTileEntity(pos.north(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.north(i))).hasModule(EnumCustomModules.HARMING))
						((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(world, pos.up(i));
				if(id == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, pos.up(i), BlockLaserBlock.POWERED)){
					if(world.getTileEntity(pos.up(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.up(i))).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos.up(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getCommandSenderName().toLowerCase()))
						return;
					BlockUtils.setBlockProperty(world, pos.up(i), BlockLaserBlock.POWERED, true, true);
					world.notifyNeighborsOfStateChange(pos.up(i), SCContent.laserBlock);
					world.scheduleUpdate(pos.up(i), SCContent.laserBlock, 50);

					if(world.getTileEntity(pos.up(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.up(i))).hasModule(EnumCustomModules.HARMING))
						((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(world, pos.down(i));
				if(id == SCContent.laserBlock && !BlockUtils.getBlockPropertyAsBoolean(world, pos.down(i), BlockLaserBlock.POWERED)){
					if(world.getTileEntity(pos.down(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.down(i))).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos.down(i), EnumCustomModules.WHITELIST).contains(((EntityLivingBase) entity).getCommandSenderName().toLowerCase()))
						return;
					BlockUtils.setBlockProperty(world, pos.down(i), BlockLaserBlock.POWERED, true, true);
					world.notifyNeighborsOfStateChange(pos.down(i), SCContent.laserBlock);
					world.scheduleUpdate(pos.down(i), SCContent.laserBlock, 50);

					if(world.getTileEntity(pos.down(i)) instanceof CustomizableSCTE && ((CustomizableSCTE) world.getTileEntity(pos.down(i))).hasModule(EnumCustomModules.HARMING))
						((EntityLivingBase) entity).attackEntityFrom(CustomDamageSources.laser, 10F);

					break;
				}
			}
		}
	}

	/**
	 * Called right before the block is destroyed by a player.  Args: world, pos, state
	 */
	@Override
	public void onBlockDestroyedByPlayer(World par1World, BlockPos pos, IBlockState state)
	{
		if(!par1World.isRemote){
			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(par1World, pos.east(i));
				if(id == SCContent.laserBlock)
					for(int j = 1; j < i; j++)
						par1World.destroyBlock(pos.east(j), false);
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(par1World, pos.west(i));
				if(id == SCContent.laserBlock)
					for(int j = 1; j < i; j++)
						par1World.destroyBlock(pos.west(j), false);
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(par1World, pos.south(i));
				if(id == SCContent.laserBlock)
					for(int j = 1; j < i; j++)
						par1World.destroyBlock(pos.south(j), false);
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(par1World, pos.north(i));
				if(id == SCContent.laserBlock)
					for(int j = 1; j < i; j++)
						par1World.destroyBlock(pos.north(j), false);
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(par1World, pos.up(i));
				if(id == SCContent.laserBlock)
					for(int j = 1; j < i; j++)
						par1World.destroyBlock(pos.up(j), false);
			}

			for(int i = 0; i < SecurityCraft.config.laserBlockRange; i++){
				Block id = BlockUtils.getBlock(par1World, pos.down(i));
				if(id == SCContent.laserBlock)
					for(int j = 1; j < i; j++)
						par1World.destroyBlock(pos.down(j), false);
			}
		}
	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, pos, state
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, BlockPos pos)
	{
		if (((Integer) par1IBlockAccess.getBlockState(pos).getValue(BOUNDTYPE)).intValue() == 1)
			setBlockBounds(0.250F, 0.000F, 0.300F, 0.750F, 1.000F, 0.700F);
		else if (((Integer) par1IBlockAccess.getBlockState(pos).getValue(BOUNDTYPE)).intValue() == 2)
			setBlockBounds(0.325F, 0.300F, 0.000F, 0.700F, 0.700F, 1.000F);
		else if (((Integer) par1IBlockAccess.getBlockState(pos).getValue(BOUNDTYPE)).intValue() == 3)
			setBlockBounds(0.000F, 0.300F, 0.300F, 1.000F, 0.700F, 0.700F);
		else
			setBlockBounds(0.250F, 0.300F, 0.300F, 0.750F, 0.700F, 0.700F);

	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(BOUNDTYPE, 1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(BOUNDTYPE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((Integer) state.getValue(BOUNDTYPE)).intValue();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {BOUNDTYPE});
	}

	@Override
	@SideOnly(Side.CLIENT)

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	public Item getItem(World par1World, BlockPos pos)
	{
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySCTE().intersectsEntities();
	}

}
