package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class InventoryScannerBlock extends DisguisableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HORIZONTAL = BooleanProperty.create("horizontal");

	public InventoryScannerBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HORIZONTAL, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(isFacingAnotherScanner(world, pos) && player instanceof ServerPlayer serverPlayer)
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(!world.isClientSide && te instanceof MenuProvider menuProvider)
				NetworkHooks.openGui(serverPlayer, menuProvider, pos);

			return InteractionResult.SUCCESS;
		}
		else {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.INVENTORY_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:invScan.notConnected"), ChatFormatting.RED);
			return InteractionResult.SUCCESS;
		}
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		super.setPlacedBy(world, pos, state, entity, stack);

		if(world.isClientSide)
			return;

		checkAndPlaceAppropriately(world, pos);
	}

	private void checkAndPlaceAppropriately(Level world, BlockPos pos)
	{
		InventoryScannerBlockEntity connectedScanner = getConnectedInventoryScanner(world, pos);
		InventoryScannerBlockEntity thisTe = (InventoryScannerBlockEntity)world.getBlockEntity(pos);

		if(connectedScanner == null || !connectedScanner.getOwner().equals(thisTe.getOwner()))
			return;

		boolean horizontal = false;

		if(connectedScanner.getBlockState().getValue(HORIZONTAL))
			horizontal = true;

		Direction facing = world.getBlockState(pos).getValue(FACING);
		int loopBoundary = facing == Direction.WEST || facing == Direction.EAST ? Math.abs(pos.getX() - connectedScanner.getBlockPos().getX()) : (facing == Direction.NORTH || facing == Direction.SOUTH ? Math.abs(pos.getZ() - connectedScanner.getBlockPos().getZ()) : 0);

		thisTe.setHorizontal(horizontal);

		for(int i = 1; i < loopBoundary; i++)
		{
			if(world.getBlockState(pos.relative(facing, i)).getBlock() == SCContent.INVENTORY_SCANNER_FIELD.get())
				return;
		}

		Option<?>[] customOptions = thisTe.customOptions();

		for(int i = 1; i < loopBoundary; i++)
		{
			BlockPos offsetPos = pos.relative(facing, i);

			world.setBlockAndUpdate(offsetPos, SCContent.INVENTORY_SCANNER_FIELD.get().defaultBlockState().setValue(FACING, facing).setValue(HORIZONTAL, horizontal));

			BlockEntity te = world.getBlockEntity(offsetPos);

			if(te instanceof IOwnable ownable)
				ownable.setOwner(thisTe.getOwner().getUUID(), thisTe.getOwner().getName());
		}

		CustomizableBlockEntity.link(thisTe, connectedScanner);

		for(ModuleType type : connectedScanner.getInsertedModules())
		{
			thisTe.insertModule(connectedScanner.getModule(type));
		}

		((BooleanOption)customOptions[0]).setValue(connectedScanner.isHorizontal());
		((BooleanOption)customOptions[1]).setValue(connectedScanner.doesFieldSolidify());
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(world.isClientSide || state.getBlock() == newState.getBlock())
			return;

		InventoryScannerBlockEntity connectedScanner = null;

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

					connectedScanner = (InventoryScannerBlockEntity)world.getBlockEntity(offsetIPos);
					break;
				}
			}
		}

		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof InventoryScannerBlockEntity te)
		{
			for(int i = 10; i < te.getContainerSize(); i++) //first 10 slots (0-9) are the prohibited slots
			{
				Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), te.getContents().get(i));
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

	private boolean isFacingAnotherScanner(Level world, BlockPos pos)
	{
		return getConnectedInventoryScanner(world, pos) != null;
	}

	public static InventoryScannerBlockEntity getConnectedInventoryScanner(Level world, BlockPos pos)
	{
		Direction facing = world.getBlockState(pos).getValue(FACING);

		for(int i = 0; i <= ConfigHandler.SERVER.inventoryScannerRange.get(); i++)
		{
			BlockPos offsetPos = pos.relative(facing, i);
			BlockState state = world.getBlockState(offsetPos);
			Block block = state.getBlock();

			if(!state.isAir() && block != SCContent.INVENTORY_SCANNER_FIELD.get() && block != SCContent.INVENTORY_SCANNER.get())
				return null;

			if(block == SCContent.INVENTORY_SCANNER.get() && state.getValue(FACING) == facing.getOpposite())
				return (InventoryScannerBlockEntity)world.getBlockEntity(offsetPos);
		}

		return null;
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
		checkAndPlaceAppropriately((Level)world, pos);
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
	public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side)
	{
		return false;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
	{
		if(!(blockAccess.getBlockEntity(pos) instanceof InventoryScannerBlockEntity te)){
			return 0;
		}

		return te.hasModule(ModuleType.REDSTONE) && te.shouldProvidePower() ? 15 : 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
	{
		return getSignal(blockState, blockAccess, pos, side);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer)
	{
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HORIZONTAL);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new InventoryScannerBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeInventoryScanner, WorldUtils::blockEntityTicker);
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
		public boolean isPowering(Level world, BlockPos pos, BlockState state, BlockEntity te)
		{
			return ((InventoryScannerBlockEntity)te).hasModule(ModuleType.REDSTONE) && ((InventoryScannerBlockEntity)te).shouldProvidePower();
		}

		@Override
		public List<Block> getBlocks()
		{
			return blocks;
		}
	}
}
