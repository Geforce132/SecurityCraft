package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InventoryScannerBlock extends DisguisableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = BooleanProperty.create("horizontal");

	public InventoryScannerBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false));
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(isFacingAnotherScanner(world, pos) && player instanceof ServerPlayerEntity)
		{
			TileEntity te = world.getBlockEntity(pos);

			if(!world.isClientSide && te instanceof INamedContainerProvider)
				NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);

			return ActionResultType.SUCCESS;
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.INVENTORY_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:invScan.notConnected"), TextFormatting.RED);
			return ActionResultType.SUCCESS;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.setPlacedBy(world, pos, state, entity, stack);

		if(world.isClientSide)
			return;

		checkAndPlaceAppropriately(world, pos);
	}

	private void checkAndPlaceAppropriately(World world, BlockPos pos)
	{
		InventoryScannerTileEntity connectedScanner = getConnectedInventoryScanner(world, pos);

		if(connectedScanner == null || !connectedScanner.getOwner().equals(((InventoryScannerTileEntity)world.getBlockEntity(pos)).getOwner()))
			return;

		boolean horizontal = false;

		if(connectedScanner.getBlockState().getValue(HORIZONTAL))
			horizontal = true;

		((InventoryScannerTileEntity)world.getBlockEntity(pos)).setHorizontal(horizontal);

		Direction facing = world.getBlockState(pos).getValue(FACING);
		int loopBoundary = facing == Direction.WEST || facing == Direction.EAST ? Math.abs(pos.getX() - connectedScanner.getBlockPos().getX()) : (facing == Direction.NORTH || facing == Direction.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getBlockPos().getZ()) : 0);

		for(int i = 1; i < loopBoundary; i++)
		{
			if(world.getBlockState(pos.relative(facing, i)).getBlock() == SCContent.INVENTORY_SCANNER_FIELD.get())
				return;
		}

		InventoryScannerTileEntity thisTe = (InventoryScannerTileEntity)world.getBlockEntity(pos);
		Option<?>[] customOptions = thisTe.customOptions();

		for(int i = 1; i < loopBoundary; i++)
		{
			BlockPos offsetPos = pos.relative(facing, i);

			world.setBlockAndUpdate(offsetPos, SCContent.INVENTORY_SCANNER_FIELD.get().defaultBlockState().setValue(FACING, facing).setValue(HORIZONTAL, horizontal));

			TileEntity te = world.getBlockEntity(offsetPos);

			if(te instanceof IOwnable)
				((IOwnable)te).setOwner(thisTe.getOwner().getUUID(), thisTe.getOwner().getName());
		}

		CustomizableTileEntity.link(thisTe, connectedScanner);

		for(ModuleType type : connectedScanner.getInsertedModules())
		{
			thisTe.insertModule(connectedScanner.getModule(type));
		}

		((BooleanOption)customOptions[0]).setValue(connectedScanner.isHorizontal());
		((BooleanOption)customOptions[1]).setValue(connectedScanner.doesFieldSolidify());
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(world.isClientSide || state.getBlock() == newState.getBlock())
			return;

		InventoryScannerTileEntity connectedScanner = null;

		for(Direction facing : Direction.Plane.HORIZONTAL)
		{
			for(int i = 1; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++)
			{
				BlockPos offsetIPos = pos.relative(facing, i);

				if(world.getBlockState(offsetIPos).getBlock() == SCContent.INVENTORY_SCANNER.get())
				{
					for(int j = 1; j < i; j++)
					{
						BlockPos offsetJPos = pos.relative(facing, j);
						BlockState field = world.getBlockState(offsetJPos);

						//checking if the field is oriented correctly
						if(field.getBlock() == SCContent.INVENTORY_SCANNER_FIELD.get())
						{
							if(facing == Direction.WEST || facing == Direction.EAST)
							{
								if(field.getValue(InventoryScannerFieldBlock.FACING) == Direction.WEST || field.getValue(InventoryScannerFieldBlock.FACING) == Direction.EAST)
									world.destroyBlock(offsetJPos, false);
							}
							else if(facing == Direction.NORTH || facing == Direction.SOUTH)
							{
								if(field.getValue(InventoryScannerFieldBlock.FACING) == Direction.NORTH || field.getValue(InventoryScannerFieldBlock.FACING) == Direction.SOUTH)
									world.destroyBlock(offsetJPos, false);
							}
						}
					}

					connectedScanner = (InventoryScannerTileEntity)world.getBlockEntity(offsetIPos);
					break;
				}
			}
		}

		TileEntity tile = world.getBlockEntity(pos);

		if(tile instanceof InventoryScannerTileEntity)
		{
			InventoryScannerTileEntity te = (InventoryScannerTileEntity)tile;

			for(int i = 10; i < te.getContainerSize(); i++) //first 10 slots (0-9) are the prohibited slots
			{
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getContents().get(i));
			}
		}

		if(connectedScanner != null)
		{
			for(int i = 0; i < connectedScanner.getContents().size(); i++)
			{
				connectedScanner.getContents().set(i, ItemStack.EMPTY);
			}
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	private boolean isFacingAnotherScanner(World world, BlockPos pos)
	{
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static InventoryScannerTileEntity getConnectedInventoryScanner(World world, BlockPos pos)
	{
		Direction facing = world.getBlockState(pos).getValue(FACING);

		for(int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++)
		{
			BlockPos offsetPos = pos.relative(facing, i);
			BlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();

			if(!state.isAir(world, offsetPos) && block != SCContent.INVENTORY_SCANNER_FIELD.get() && block != SCContent.INVENTORY_SCANNER.get())
				return null;

			if(block == SCContent.INVENTORY_SCANNER.get() && state.getValue(FACING) == facing.getOpposite())
				return (InventoryScannerTileEntity)world.getBlockEntity(offsetPos);
		}

		return null;
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		checkAndPlaceAppropriately((World)world, pos);
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean isSignalSource(BlockState state)
	{
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
	{
		return false;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		if(!(blockAccess.getBlockEntity(pos) instanceof InventoryScannerTileEntity)){
			return 0;
		}

		return (((InventoryScannerTileEntity) blockAccess.getBlockEntity(pos)).hasModule(ModuleType.REDSTONE) && ((InventoryScannerTileEntity) blockAccess.getBlockEntity(pos)).shouldProvidePower())? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		return getSignal(blockState, blockAccess, pos, side);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HORIZONTAL);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new InventoryScannerTileEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public static class DoorActivator implements IDoorActivator
	{
		private List<Block> blocks = Arrays.asList(SCContent.INVENTORY_SCANNER.get());

		@Override
		public boolean isPowering(World world, BlockPos pos, BlockState state, TileEntity te)
		{
			return ((InventoryScannerTileEntity)te).hasModule(ModuleType.REDSTONE) && ((InventoryScannerTileEntity)te).shouldProvidePower();
		}

		@Override
		public List<Block> getBlocks()
		{
			return blocks;
		}
	}
}
