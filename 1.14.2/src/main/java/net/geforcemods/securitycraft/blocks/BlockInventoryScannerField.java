package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

public class BlockInventoryScannerField extends ContainerBlock implements IIntersectable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE_EW = Block.makeCuboidShape(0, 0, 6, 16, 16, 10);
	private static final VoxelShape SHAPE_NS = Block.makeCuboidShape(6, 0, 0, 10, 16, 16);

	public BlockInventoryScannerField(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return VoxelShapes.empty();
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity)
	{
		TileEntityInventoryScanner connectedScanner = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null)
			return;

		if(entity instanceof PlayerEntity)
		{
			if(ModuleUtils.checkForModule(world, connectedScanner.getPos(), (PlayerEntity)entity, EnumCustomModules.WHITELIST))
				return;

			for(int i = 0; i < 10; i++)
			{
				for(int j = 0; j < ((PlayerEntity)entity).inventory.mainInventory.size(); j++)
				{
					if(!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((PlayerEntity)entity).inventory.mainInventory.get(j).isEmpty())
						checkInventory((PlayerEntity)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
				}
			}
		}
		else if(entity instanceof ItemEntity)
		{
			for(int i = 0; i < 10; i++)
			{
				if(!connectedScanner.getStackInSlotCopy(i).isEmpty() && !((ItemEntity)entity).getItem().isEmpty())
					checkItemEntity((ItemEntity)entity, connectedScanner, connectedScanner.getStackInSlotCopy(i));
			}
		}
	}

	public static void checkInventory(PlayerEntity entity, TileEntityInventoryScanner te, ItemStack stack)
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

	public static void checkItemEntity(ItemEntity entity, TileEntityInventoryScanner te, ItemStack stack)
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
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state)
	{
		if(!world.isRemote())
		{
			for(int i = 0; i < CommonConfig.CONFIG.inventoryScannerRange.get(); i++)
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

			for(int i = 0; i < CommonConfig.CONFIG.inventoryScannerRange.get(); i++)
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

			for(int i = 0; i < CommonConfig.CONFIG.inventoryScannerRange.get(); i++)
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

			for(int i = 0; i < CommonConfig.CONFIG.inventoryScannerRange.get(); i++)
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
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		Direction facing = source.getBlockState(pos).get(FACING);

		if (facing == Direction.EAST || facing == Direction.WEST)
			return SHAPE_EW; //ew
		else if (facing == Direction.NORTH || facing == Direction.SOUTH)
			return SHAPE_NS; //ns
		return VoxelShapes.fullCube();
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntitySCTE().intersectsEntities();
	}

}
