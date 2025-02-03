package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.GlowDisplayCaseBlockEntity;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DisplayCaseBlock extends OwnableBlock implements SimpleWaterloggedBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<AttachFace> ATTACH_FACE = BlockStateProperties.ATTACH_FACE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final VoxelShape FLOOR = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
	public static final VoxelShape CEILING = Block.box(2.0D, 10.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	public static final VoxelShape WALL_N = Block.box(2.0D, 2.0D, 10.0D, 14.0D, 14.0D, 16.0D);
	public static final VoxelShape WALL_E = Block.box(0.0D, 2.0D, 2.0D, 6.0D, 14.0D, 14.0D);
	public static final VoxelShape WALL_S = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 6.0D);
	public static final VoxelShape WALL_W = Block.box(10.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
	private final boolean glowing;

	public DisplayCaseBlock(BlockBehaviour.Properties properties, boolean glowing) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACH_FACE, AttachFace.WALL).setValue(WATERLOGGED, false));
		this.glowing = glowing;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(ATTACH_FACE)) {
			case FLOOR -> FLOOR;
			case CEILING -> CEILING;
			case WALL -> switch (state.getValue(FACING)) {
				case NORTH -> WALL_N;
				case EAST -> WALL_E;
				case SOUTH -> WALL_S;
				case WEST -> WALL_W;
				default -> Shapes.empty();
			};
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level level = ctx.getLevel();
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
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = getConnectedDirection(state).getOpposite();
		BlockPos relativePos = pos.relative(direction);

		return level.getBlockState(relativePos).isFaceSturdy(level, relativePos, direction.getOpposite());
	}

	@Override
	public InteractionResult useItemOn(ItemStack heldStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide && !heldStack.isEmpty() && level.getBlockEntity(pos) instanceof DisplayCaseBlockEntity be && be.isOpen()) {
			ItemStack displayedStack = be.getDisplayedStack();

			if (displayedStack.isEmpty()) {
				ItemStack toAdd;

				if (player.isCreative()) {
					toAdd = heldStack.copy();
					toAdd.setCount(1);
				}
				else
					toAdd = heldStack.split(1);

				be.setDisplayedStack(toAdd);
				level.playSound(null, pos, glowing ? SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof DisplayCaseBlockEntity be) {
			if (be.isLocked() && be.disableInteractionWhenLocked(level, pos, player)) {
				MutableComponent blockName = Utils.localize(getDescriptionId());

				PlayerUtils.sendMessageToPlayer(player, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), ChatFormatting.DARK_RED, false);
				return InteractionResult.SUCCESS;
			}

			if (be.isOpen()) {
				ItemStack displayedStack = be.getDisplayedStack();

				if (!displayedStack.isEmpty() && player.isShiftKeyDown()) {
					player.addItem(displayedStack);
					level.playSound(null, pos, glowing ? SoundEvents.GLOW_ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
					be.setDisplayedStack(ItemStack.EMPTY);
					return InteractionResult.SUCCESS;
				}

				be.setOpen(false);
			}
			else {
				if (be.isDisabled())
					player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
				else if (be.verifyPasscodeSet(level, pos, be, player)) {
					if (be.isDenied(player)) {
						if (be.sendsDenylistMessage())
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
					}
					else if (be.isAllowed(player)) {
						if (be.sendsAllowlistMessage())
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

						activate(be);
					}
					else
						be.openPasscodeGUI(level, pos, player);
				}
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void activate(DisplayCaseBlockEntity be) {
		be.setOpen(true);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.getInventory().clear();

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);

			if (be instanceof DisplayCaseBlockEntity displayCase)
				Block.popResource(level, pos, displayCase.getDisplayedStack());

			if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && be instanceof IModuleInventory inv)
				inv.dropAllModules();

			if (be instanceof IPasscodeProtected passcodeProtected)
				SaltData.removeSalt(passcodeProtected.getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
		if (state.getValue(WATERLOGGED))
			tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return getConnectedDirection(state).getOpposite() == facing && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, tickAccess, pos, facing, facingPos, facingState, random);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof DisplayCaseBlockEntity be) {
			ItemStack displayedStack = be.getDisplayedStack();

			if (!displayedStack.isEmpty() && be.isOpen() && !Screen.hasControlDown())
				return displayedStack;
		}

		return super.getCloneItemStack(state, target, level, pos, player);
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return glowing ? new GlowDisplayCaseBlockEntity(pos, state) : new DisplayCaseBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) {
			if (glowing)
				return createTickerHelper(type, SCContent.GLOW_DISPLAY_CASE_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
			else
				return createTickerHelper(type, SCContent.DISPLAY_CASE_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
		}

		return null;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ATTACH_FACE, WATERLOGGED);
	}

	private Direction getConnectedDirection(BlockState state) {
		return switch (state.getValue(ATTACH_FACE)) {
			case CEILING -> Direction.DOWN;
			case FLOOR -> Direction.UP;
			default -> state.getValue(FACING);
		};
	}
}
