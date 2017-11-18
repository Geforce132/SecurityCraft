package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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
import net.minecraft.init.Blocks;
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

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborChange(IBlockAccess w, BlockPos pos, BlockPos neighbor) {
		World world = (World)w;

		if(world.isRemote)
			return;
		else if(!Utils.hasInventoryScannerFacingBlock(world, pos))
			world.destroyBlock(pos, false);
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(entity instanceof EntityPlayer){
			if(world.getTileEntity(pos.west()) != null && world.getTileEntity(pos.west()) instanceof TileEntityInventoryScanner){
				if(ModuleUtils.checkForModule(world, pos.west(), ((EntityPlayer) entity), EnumCustomModules.WHITELIST))
					return;
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < ((EntityPlayer) entity).inventory.mainInventory.length; j++)
						if(((TileEntityInventoryScanner)world.getTileEntity(pos.west())).getStackInSlotCopy(i) != null)
							if(((EntityPlayer) entity).inventory.mainInventory[j] != null)
								checkInventory(((EntityPlayer) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.west())), ((TileEntityInventoryScanner)world.getTileEntity(pos.west())).getStackInSlotCopy(i));
			}else if(world.getTileEntity(pos.east()) != null && world.getTileEntity(pos.east()) instanceof TileEntityInventoryScanner){
				if(ModuleUtils.checkForModule(world, pos.east(), ((EntityPlayer) entity), EnumCustomModules.WHITELIST))
					return;
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < ((EntityPlayer) entity).inventory.mainInventory.length; j++)
						if(((TileEntityInventoryScanner)world.getTileEntity(pos.east())).getStackInSlotCopy(i) != null)
							if(((EntityPlayer) entity).inventory.mainInventory[j] != null)
								checkInventory(((EntityPlayer) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.east())), ((TileEntityInventoryScanner)world.getTileEntity(pos.east())).getStackInSlotCopy(i));
			}else if(world.getTileEntity(pos.north()) != null && world.getTileEntity(pos.north()) instanceof TileEntityInventoryScanner){
				if(ModuleUtils.checkForModule(world, pos.north(), ((EntityPlayer) entity), EnumCustomModules.WHITELIST))
					return;
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < ((EntityPlayer) entity).inventory.mainInventory.length; j++)
						if(((TileEntityInventoryScanner)world.getTileEntity(pos.north())).getStackInSlotCopy(i) != null)
							if(((EntityPlayer) entity).inventory.mainInventory[j] != null)
								checkInventory(((EntityPlayer) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.north())), ((TileEntityInventoryScanner)world.getTileEntity(pos.north())).getStackInSlotCopy(i));
			}else if(world.getTileEntity(pos.south()) != null && world.getTileEntity(pos.south()) instanceof TileEntityInventoryScanner){
				if(ModuleUtils.checkForModule(world, pos.south(), ((EntityPlayer) entity), EnumCustomModules.WHITELIST))
					return;
				for(int i = 0; i < 10; i++)
					for(int j = 0; j < ((EntityPlayer) entity).inventory.mainInventory.length; j++)
						if(((TileEntityInventoryScanner)world.getTileEntity(pos.south())).getStackInSlotCopy(i) != null)
							if(((EntityPlayer) entity).inventory.mainInventory[j] != null)
								checkInventory(((EntityPlayer) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.south())), ((TileEntityInventoryScanner)world.getTileEntity(pos.south())).getStackInSlotCopy(i));
			}
			//******************************************
		}else if(entity instanceof EntityItem)
			if(world.getTileEntity(pos.west()) != null && world.getTileEntity(pos.west()) instanceof TileEntityInventoryScanner){
				for(int i = 0; i < 10; i++)
					if(((TileEntityInventoryScanner)world.getTileEntity(pos.west())).getStackInSlotCopy(i) != null)
						if(((EntityItem) entity).getEntityItem() != null)
							checkEntity(((EntityItem) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.west())).getStackInSlotCopy(i));
			}else if(world.getTileEntity(pos.east()) != null && world.getTileEntity(pos.east()) instanceof TileEntityInventoryScanner){
				for(int i = 0; i < 10; i++)
					if(((TileEntityInventoryScanner)world.getTileEntity(pos.east())).getStackInSlotCopy(i) != null)
						if(((EntityItem) entity).getEntityItem() != null)
							checkEntity(((EntityItem) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.east())).getStackInSlotCopy(i));
			}else if(world.getTileEntity(pos.north()) != null && world.getTileEntity(pos.north()) instanceof TileEntityInventoryScanner){
				for(int i = 0; i < 10; i++)
					if(((TileEntityInventoryScanner)world.getTileEntity(pos.north())).getStackInSlotCopy(i) != null)
						if(((EntityItem) entity).getEntityItem() != null)
							checkEntity(((EntityItem) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.north())).getStackInSlotCopy(i));
			}else if(world.getTileEntity(pos.south()) != null && world.getTileEntity(pos.south()) instanceof TileEntityInventoryScanner)
				for(int i = 0; i < 10; i++)
					if(((TileEntityInventoryScanner)world.getTileEntity(pos.south())).getStackInSlotCopy(i) != null)
						if(((EntityItem) entity).getEntityItem() != null)
							checkEntity(((EntityItem) entity), ((TileEntityInventoryScanner)world.getTileEntity(pos.south())).getStackInSlotCopy(i));
	}

	public static void checkInventory(EntityPlayer par1EntityPlayer, TileEntityInventoryScanner par2TileEntity, ItemStack par3){
		//    	Block block = null;
		//		Item item = null;
		//		boolean flag = false;
		//
		//		if(hasMultipleItemStacks(par3)){
		//			if (Item.itemRegistry.containsKey(par3))
		//	        {
		//
		//	            item = (Item)Item.itemRegistry.getObject(par3);
		//	            flag = true;
		//	        }
		//		}
		//
		//		if (Block.blockRegistry.containsKey(par3) && !flag)
		//        {
		//
		//            block = (Block)Block.blockRegistry.getObject(par3);
		//        }
		//
		//		if (Item.itemRegistry.containsKey(par3) && !flag)
		//        {
		//
		//            item = (Item)Item.itemRegistry.getObject(par3);
		//        }
		if(par2TileEntity.getType().matches("redstone")){
			for(int i = 1; i <= par1EntityPlayer.inventory.mainInventory.length; i++)
				if(par1EntityPlayer.inventory.mainInventory[i - 1] != null)
					if(par1EntityPlayer.inventory.mainInventory[i - 1].getItem() == par3.getItem()){
						if(!par2TileEntity.shouldProvidePower())
							par2TileEntity.setShouldProvidePower(true);

						mod_SecurityCraft.log("Running te update");
						par2TileEntity.setCooldown(60);
						checkAndUpdateTEAppropriately(par2TileEntity.getWorld(), par2TileEntity.getPos(), par2TileEntity);
						BlockUtils.updateAndNotify(par2TileEntity.getWorld(), par2TileEntity.getPos(), par2TileEntity.getWorld().getBlockState(par2TileEntity.getPos()).getBlock(), 1, true);
						mod_SecurityCraft.log("Emitting redstone on the " + FMLCommonHandler.instance().getEffectiveSide() + " side. (te coords: " + Utils.getFormattedCoordinates(par2TileEntity.getPos()));
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

	private static void checkAndUpdateTEAppropriately(World par1World, BlockPos pos, TileEntityInventoryScanner par5TileEntityIS) {
		mod_SecurityCraft.log("Updating te");
		if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.west()) == Blocks.AIR && par1World.getBlockState(pos.west(2)).getValue(FACING) == EnumFacing.EAST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setShouldProvidePower(true);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setCooldown(60);
			BlockUtils.updateAndNotify(par1World, pos.west(2), BlockUtils.getBlock(par1World, pos), 1, true);
		}else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.east()) == Blocks.AIR && par1World.getBlockState(pos.east(2)).getValue(FACING) == EnumFacing.WEST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setShouldProvidePower(true);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setCooldown(60);
			BlockUtils.updateAndNotify(par1World, pos.east(2), BlockUtils.getBlock(par1World, pos), 1, true);
		}else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.north()) == Blocks.AIR && par1World.getBlockState(pos.north(2)).getValue(FACING) == EnumFacing.SOUTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setShouldProvidePower(true);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setCooldown(60);
			BlockUtils.updateAndNotify(par1World, pos.north(2), BlockUtils.getBlock(par1World, pos), 1, true);
		}else if(par1World.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.south()) == Blocks.AIR && par1World.getBlockState(pos.south(2)).getValue(FACING) == EnumFacing.NORTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setShouldProvidePower(true);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setCooldown(60);
			BlockUtils.updateAndNotify(par1World, pos.south(2), BlockUtils.getBlock(par1World, pos), 1, true);

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
