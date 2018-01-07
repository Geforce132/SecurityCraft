package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInventoryScannerField extends BlockContainer implements IIntersectable {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockInventoryScannerField(Material par2Material) {
		super(par2Material);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if(entity instanceof EntityPlayer)
		{
			if(ModuleUtils.checkForModule(world, connectedScanner.getPos(), (EntityPlayer)entity, EnumCustomModules.WHITELIST))
				return;

			for(int i = 0; i < 10; i++)
			{
				for(int j = 0; j < ((EntityPlayer)entity).inventory.mainInventory.length; j++)
				{
					if(connectedScanner.getStackInSlotCopy(i) != null && ((EntityPlayer)entity).inventory.mainInventory[j] != null)
						checkInventory((EntityPlayer)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
				}
			}
		}
		else if(entity instanceof EntityItem)
		{
			for(int i = 0; i < 10; i++)
			{
				if(connectedScanner.getStackInSlotCopy(i) != null && ((EntityItem)entity).getEntityItem() != null)
					checkEntity((EntityItem)entity, connectedScanner.getStackInSlotCopy(i));
			}
		}
	}

	public static void checkInventory(EntityPlayer par1EntityPlayer, TileEntityInventoryScanner par2TileEntity, ItemStack par3){
		if(par2TileEntity.getType().matches("redstone")){
			for(int i = 1; i <= par1EntityPlayer.inventory.mainInventory.length; i++)
				if(par1EntityPlayer.inventory.mainInventory[i - 1] != null)
					if(par1EntityPlayer.inventory.mainInventory[i - 1].getItem() == par3.getItem()){
						if(!par2TileEntity.shouldProvidePower())
							par2TileEntity.setShouldProvidePower(true);

						SecurityCraft.log("Running te update");
						par2TileEntity.setCooldown(60);
						checkAndUpdateTEAppropriately(par2TileEntity);
						BlockUtils.updateAndNotify(par2TileEntity.getWorld(), par2TileEntity.getPos(), par2TileEntity.getWorld().getBlockState(par2TileEntity.getPos()).getBlock(), 1, true);
						SecurityCraft.log("Emitting redstone on the " + FMLCommonHandler.instance().getEffectiveSide() + " side. (te coords: " + Utils.getFormattedCoordinates(par2TileEntity.getPos()));
					}
		}else if(par2TileEntity.getType().matches("check"))
			for(int i = 1; i <= par1EntityPlayer.inventory.mainInventory.length; i++)
				if(par1EntityPlayer.inventory.mainInventory[i - 1] != null){
					if(((CustomizableSCTE) par2TileEntity).hasModule(EnumCustomModules.SMART) && ItemStack.areItemStacksEqual(par1EntityPlayer.inventory.mainInventory[i - 1], par3) && ItemStack.areItemStackTagsEqual(par1EntityPlayer.inventory.mainInventory[i - 1], par3)){
						par1EntityPlayer.inventory.mainInventory[i - 1] = null;
						continue;
					}

					if(!((CustomizableSCTE) par2TileEntity).hasModule(EnumCustomModules.SMART) && par1EntityPlayer.inventory.mainInventory[i - 1].getItem() == par3.getItem())
						par1EntityPlayer.inventory.mainInventory[i - 1] = null;
				}
	}

	public static void checkEntity(EntityItem par1EntityItem, ItemStack par2){
		if(par1EntityItem.getEntityItem().getItem() == par2.getItem())
			par1EntityItem.setDead();

	}

	private static void checkAndUpdateTEAppropriately(TileEntityInventoryScanner te)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(te.getWorld(), te.getPos());

		te.setShouldProvidePower(true);
		te.setCooldown(60);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), te.getBlockType(), 1, true);
		connectedScanner.setShouldProvidePower(true);
		connectedScanner.setCooldown(60);
		BlockUtils.updateAndNotify(connectedScanner.getWorld(), connectedScanner.getPos(), connectedScanner.getBlockType(), 1, true);
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
	{
		if(!worldIn.isRemote)
		{
			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(BlockUtils.getBlock(worldIn, pos.west(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						worldIn.destroyBlock(pos.west(j), false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(BlockUtils.getBlock(worldIn, pos.east(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						worldIn.destroyBlock(pos.east(j), false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(BlockUtils.getBlock(worldIn, pos.north(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						worldIn.destroyBlock(pos.north(j), false);
					}

					break;
				}
			}

			for(int i = 0; i < SecurityCraft.config.inventoryScannerRange; i++)
			{
				if(BlockUtils.getBlock(worldIn, pos.south(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						worldIn.destroyBlock(pos.south(j), false);
					}

					break;
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if (source.getBlockState(pos).getValue(FACING) == EnumFacing.EAST || source.getBlockState(pos).getValue(FACING) == EnumFacing.WEST)
			return new AxisAlignedBB(0.000F, 0.000F, 0.400F, 1.000F, 1.000F, 0.600F); //ew
		else if (source.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH || source.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
			return new AxisAlignedBB(0.400F, 0.000F, 0.000F, 0.600F, 1.000F, 1.000F); //ns
		return state.getBoundingBox(source, pos);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@SideOnly(Side.CLIENT)

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySCTE().intersectsEntities();
	}

}
