package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class DisplayCaseBlock extends OwnableBlock implements IWaterLoggable {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<AttachFace> ATTACH_FACE = BlockStateProperties.ATTACH_FACE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape FLOOR = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
	public static final VoxelShape CEILING = Block.box(2.0D, 10.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	public static final VoxelShape WALL_N = Block.box(2.0D, 2.0D, 10.0D, 14.0D, 14.0D, 16.0D);
	public static final VoxelShape WALL_E = Block.box(0.0D, 2.0D, 2.0D, 6.0D, 14.0D, 14.0D);
	public static final VoxelShape WALL_S = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 6.0D);
	public static final VoxelShape WALL_W = Block.box(10.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);

	public DisplayCaseBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACH_FACE, AttachFace.WALL).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		switch (state.getValue(ATTACH_FACE)) {
			case FLOOR:
				return FLOOR;
			case CEILING:
				return CEILING;
			case WALL:
				switch (state.getValue(FACING)) {
					case NORTH:
						return WALL_N;
					case EAST:
						return WALL_E;
					case SOUTH:
						return WALL_S;
					case WEST:
						return WALL_W;
					default:
						return VoxelShapes.empty();
				}
		}

		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();

		for (Direction direction : ctx.getNearestLookingDirections()) {
			BlockState state;

			if (direction.getAxis() == Direction.Axis.Y)
				state = defaultBlockState().setValue(ATTACH_FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
			else
				state = defaultBlockState().setValue(ATTACH_FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());

			if (state.canSurvive(level, pos))
				return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
		}

		return null;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		Direction direction = getConnectedDirection(state).getOpposite();
		BlockPos relativePos = pos.relative(direction);

		return level.getBlockState(relativePos).isFaceSturdy(level, relativePos, direction.getOpposite());
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof DisplayCaseBlockEntity) {
				DisplayCaseBlockEntity be = (DisplayCaseBlockEntity) te;
				ItemStack heldStack = player.getItemInHand(hand);

				if (be.isLocked() && be.disableInteractionWhenLocked(level, pos, player)) {
					TranslationTextComponent blockName = Utils.localize(getDescriptionId());

					PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
					return ActionResultType.SUCCESS;
				}

				if (be.isOpen()) {
					ItemStack displayedStack = be.getDisplayedStack();

					if (displayedStack.isEmpty()) {
						if (!heldStack.isEmpty()) {
							ItemStack toAdd;

							if (player.isCreative()) {
								toAdd = heldStack.copy();
								toAdd.setCount(1);
							}
							else
								toAdd = heldStack.split(1);

							be.setDisplayedStack(toAdd);
							level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
							return ActionResultType.SUCCESS;
						}
					}
					else if (player.isShiftKeyDown()) {
						player.addItem(displayedStack);
						level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
						be.setDisplayedStack(ItemStack.EMPTY);
						return ActionResultType.SUCCESS;
					}

					be.setOpen(false);
				}
				else {
					if (be.isDisabled())
						player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else if (be.verifyPasscodeSet(level, pos, be, player)) {
						if (be.isDenied(player)) {
							if (be.sendsDenylistMessage())
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
						}
						else if (be.isAllowed(player)) {
							if (be.sendsAllowlistMessage())
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

							activate(be);
						}
						else if (player.getItemInHand(hand).getItem() != SCContent.CODEBREAKER.get())
							be.openPasscodeGUI(level, pos, player);
					}
				}
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(DisplayCaseBlockEntity be) {
		be.setOpen(true);
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof DisplayCaseBlockEntity)
				Block.popResource(level, pos, ((DisplayCaseBlockEntity) be).getDisplayedStack());

			if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && be instanceof IModuleInventory)
				((IModuleInventory) be).dropAllModules();

			if (be instanceof IPasscodeProtected)
				SaltData.removeSalt(((IPasscodeProtected) be).getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos pos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return getConnectedDirection(state).getOpposite() == facing && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, pos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof DisplayCaseBlockEntity) {
			DisplayCaseBlockEntity be = (DisplayCaseBlockEntity) te;
			ItemStack displayedStack = be.getDisplayedStack();

			if (!displayedStack.isEmpty() && be.isOpen() && !Screen.hasControlDown())
				return displayedStack;
		}

		return super.getPickBlock(state, target, level, pos, player);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new DisplayCaseBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, ATTACH_FACE, WATERLOGGED);
	}

	private Direction getConnectedDirection(BlockState state) {
		switch (state.getValue(ATTACH_FACE)) {
			case CEILING:
				return Direction.DOWN;
			case FLOOR:
				return Direction.UP;
			default:
				return state.getValue(FACING);
		}
	}
}
