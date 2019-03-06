package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

public class BlockInventoryScannerField extends BlockContainer implements IIntersectable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public BlockInventoryScannerField(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH));
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState blockState, IBlockReader world, BlockPos pos)
	{
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
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
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		if(entity instanceof EntityPlayer)
		{
			if(ModuleUtils.checkForModule(world, connectedScanner.getPos(), (EntityPlayer)entity, EnumCustomModules.WHITELIST))
				return;

			for(int i = 0; i < 10; i++)
			{
				for(int j = 0; j < ((EntityPlayer)entity).inventory.mainInventory.size(); j++)
				{
					if(!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((EntityPlayer)entity).inventory.mainInventory.get(j).isEmpty())
						checkInventory((EntityPlayer)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
				}
			}
		}
		else if(entity instanceof EntityItem)
		{
			for(int i = 0; i < 10; i++)
			{
				if(!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((EntityItem)entity).getItem().isEmpty())
					checkEntityItem((EntityItem)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
			}
		}
	}

	public static void checkInventory(EntityPlayer entity, TileEntityInventoryScanner te, ItemStack stack)
	{
		if(te.getScanType().equals("redstone"))
		{
			for(int i = 1; i <= entity.inventory.mainInventory.size(); i++)
			{
				if(!entity.inventory.mainInventory.get(i - 1).isEmpty())
				{
					if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.inventory.mainInventory.get(i - 1), stack) && ItemStack.areItemStackTagsEqual(entity.inventory.mainInventory.get(i - 1), stack))
							|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.inventory.mainInventory.get(i - 1).getItem() == stack.getItem()))
					{
						updateInventoryScannerPower(te);
					}
				}
			}
		}
		else if(te.getScanType().equals("check"))
		{
			for(int i = 1; i <= entity.inventory.mainInventory.size(); i++)
			{
				if(!entity.inventory.mainInventory.get(i - 1).isEmpty())
				{
					if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.inventory.mainInventory.get(i - 1), stack) && ItemStack.areItemStackTagsEqual(entity.inventory.mainInventory.get(i - 1), stack))
							|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.inventory.mainInventory.get(i - 1).getItem() == stack.getItem()))
					{
						if(te.hasModule(EnumCustomModules.STORAGE))
							te.addItemToStorage(entity.inventory.mainInventory.get(i - 1));

						entity.inventory.mainInventory.set(i - 1, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	public static void checkEntityItem(EntityItem entity, TileEntityInventoryScanner te, ItemStack stack)
	{
		if(te.getScanType().equals("redstone"))
		{
			if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.getItem(), stack) && ItemStack.areItemStackTagsEqual(entity.getItem(), stack))
					|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.getItem().getItem() == stack.getItem()))
			{
				updateInventoryScannerPower(te);
			}
		}
		else if(te.getScanType().equals("check"))
		{
			if((((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && areItemStacksEqual(entity.getItem(), stack) && ItemStack.areItemStackTagsEqual(entity.getItem(), stack))
					|| (!((CustomizableSCTE) te).hasModule(EnumCustomModules.SMART) && entity.getItem().getItem() == stack.getItem()))
			{
				if(te.hasModule(EnumCustomModules.STORAGE))
					te.addItemToStorage(entity.getItem());

				entity.remove();
			}
		}
	}

	private static void updateInventoryScannerPower(TileEntityInventoryScanner te)
	{
		if(!te.shouldProvidePower())
			te.setShouldProvidePower(true);

		SecurityCraft.log("Running te update");
		te.setCooldown(60);
		checkAndUpdateTEAppropriately(te);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()).getBlock(), 1, true);
		SecurityCraft.log("Emitting redstone on the " + (te.getWorld().isRemote ? LogicalSide.CLIENT : LogicalSide.SERVER) + " side. (te coords: " + Utils.getFormattedCoordinates(te.getPos()));
	}

	/**
	 * See {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} but without size restriction
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		ItemStack s1 = stack1.copy();
		ItemStack s2 = stack2.copy();

		s1.setCount(1);
		s2.setCount(1);
		return ItemStack.areItemStacksEqual(s1, s2);
	}

	private static void checkAndUpdateTEAppropriately(TileEntityInventoryScanner te)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(te.getWorld(), te.getPos());

		if(connectedScanner == null)
			return;

		te.setShouldProvidePower(true);
		te.setCooldown(60);
		BlockUtils.updateAndNotify(te.getWorld(), te.getPos(), te.getBlockState().getBlock(), 1, true);
		connectedScanner.setShouldProvidePower(true);
		connectedScanner.setCooldown(60);
		BlockUtils.updateAndNotify(connectedScanner.getWorld(), connectedScanner.getPos(), connectedScanner.getBlockState().getBlock(), 1, true);
	}

	@Override
	public void onPlayerDestroy(IWorld world, BlockPos pos, IBlockState state)
	{
		if(!world.isRemote())
		{
			for(int i = 0; i < ServerConfig.CONFIG.inventoryScannerRange.get(); i++)
			{
				if(BlockUtils.getBlock(world, pos.west(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.destroyBlock(pos.west(j), false);
					}

					break;
				}
			}

			for(int i = 0; i < ServerConfig.CONFIG.inventoryScannerRange.get(); i++)
			{
				if(BlockUtils.getBlock(world, pos.east(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.destroyBlock(pos.east(j), false);
					}

					break;
				}
			}

			for(int i = 0; i < ServerConfig.CONFIG.inventoryScannerRange.get(); i++)
			{
				if(BlockUtils.getBlock(world, pos.north(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.destroyBlock(pos.north(j), false);
					}

					break;
				}
			}

			for(int i = 0; i < ServerConfig.CONFIG.inventoryScannerRange.get(); i++)
			{
				if(BlockUtils.getBlock(world, pos.south(i)) == SCContent.inventoryScanner)
				{
					for(int j = 1; j < i; j++)
					{
						world.destroyBlock(pos.south(j), false);
					}

					break;
				}
			}
		}
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		//		if (source.getBlockState(pos).getValue(FACING) == EnumFacing.EAST || source.getBlockState(pos).getValue(FACING) == EnumFacing.WEST)
		//			return new AxisAlignedBB(0.000F, 0.000F, 0.400F, 1.000F, 1.000F, 0.600F); //ew
		//		else if (source.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH || source.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
		//			return new AxisAlignedBB(0.400F, 0.000F, 0.000F, 0.600F, 1.000F, 1.000F); //ns
		//		return state.getBoundingBox(source, pos);
		return VoxelShapes.fullCube();
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, EntityPlayer player)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntitySCTE().intersectsEntities();
	}

}
