package net.geforcemods.securitycraft.blocks;

import java.util.Random;
import java.util.stream.Stream;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeConvertible;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.blockentities.AbstractKeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractKeypadFurnaceBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final VoxelShape NORTH_OPEN = Stream.of(Block.box(11, 1, 1, 12, 2, 2), Block.box(0, 0, 2, 16, 16, 16), Block.box(4, 1, 0, 12, 2, 1), Block.box(4, 1, 1, 5, 2, 2)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape NORTH_CLOSED = Stream.of(Block.box(4, 14, 1, 5, 15, 2), Block.box(11, 14, 1, 12, 15, 2), Block.box(0, 0, 2, 16, 16, 16), Block.box(4, 14, 0, 12, 15, 1)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape EAST_OPEN = Stream.of(Block.box(14, 1, 11, 15, 2, 12), Block.box(0, 0, 0, 14, 16, 16), Block.box(15, 1, 4, 16, 2, 12), Block.box(14, 1, 4, 15, 2, 5)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape EAST_CLOSED = Stream.of(Block.box(14, 14, 4, 15, 15, 5), Block.box(14, 14, 11, 15, 15, 12), Block.box(0, 0, 0, 14, 16, 16), Block.box(15, 14, 4, 16, 15, 12)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SOUTH_OPEN = Stream.of(Block.box(4, 1, 14, 5, 2, 15), Block.box(0, 0, 0, 16, 16, 14), Block.box(4, 1, 15, 12, 2, 16), Block.box(11, 1, 14, 12, 2, 15)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SOUTH_CLOSED = Stream.of(Block.box(11, 14, 14, 12, 15, 15), Block.box(4, 14, 14, 5, 15, 15), Block.box(0, 0, 0, 16, 16, 14), Block.box(4, 14, 15, 12, 15, 16)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape WEST_OPEN = Stream.of(Block.box(1, 1, 4, 2, 2, 5), Block.box(2, 0, 0, 16, 16, 16), Block.box(0, 1, 4, 1, 2, 12), Block.box(1, 1, 11, 2, 2, 12)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape WEST_CLOSED = Stream.of(Block.box(1, 14, 11, 2, 15, 12), Block.box(1, 14, 4, 2, 15, 5), Block.box(2, 0, 0, 16, 16, 16), Block.box(0, 14, 4, 1, 15, 12)).reduce((v1, v2) -> VoxelShapes.joinUnoptimized(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape NORTH_COLLISION = Block.box(0, 0, 2, 16, 16, 16);
	private static final VoxelShape EAST_COLLISION = Block.box(0, 0, 0, 14, 16, 16);
	private static final VoxelShape SOUTH_COLLISION = Block.box(0, 0, 0, 16, 16, 14);
	private static final VoxelShape WEST_COLLISION = Block.box(2, 0, 0, 16, 16, 16);

	protected AbstractKeypadFurnaceBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(LIT, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));

		if (stack.hasCustomHoverName()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof LockableTileEntity)
				((LockableTileEntity) te).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);

		switch (disguisedState.getValue(FACING)) {
			case NORTH:
				if (disguisedState.getValue(OPEN))
					return NORTH_OPEN;
				else
					return NORTH_CLOSED;
			case EAST:
				if (disguisedState.getValue(OPEN))
					return EAST_OPEN;
				else
					return EAST_CLOSED;
			case SOUTH:
				if (disguisedState.getValue(OPEN))
					return SOUTH_OPEN;
				else
					return SOUTH_CLOSED;
			case WEST:
				if (disguisedState.getValue(OPEN))
					return WEST_OPEN;
				else
					return WEST_CLOSED;
			default:
				return VoxelShapes.block();
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);

		switch (state.getValue(FACING)) {
			case NORTH:
				return NORTH_COLLISION;
			case EAST:
				return EAST_COLLISION;
			case SOUTH:
				return SOUTH_COLLISION;
			case WEST:
				return WEST_COLLISION;
			default:
				return VoxelShapes.block();
		}
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IInventory) {
				InventoryHelper.dropContents(level, pos, (IInventory) te);
				level.updateNeighbourForOutputSignal(pos, this);
			}

			if (te instanceof IPasscodeProtected)
				SaltData.removeSalt(((IPasscodeProtected) te).getSaltKey());
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide) {
			AbstractKeypadFurnaceBlockEntity be = (AbstractKeypadFurnaceBlockEntity) level.getBlockEntity(pos);

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

					activate(level, pos, player);
				}
				else if (player.getItemInHand(hand).getItem() != SCContent.CODEBREAKER.get())
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(World level, BlockPos pos, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof INamedContainerProvider)
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof AbstractKeypadFurnaceBlockEntity)
			((AbstractKeypadFurnaceBlockEntity) te).recheckOpen();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(OPEN, false).setValue(LIT, false);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, LIT, WATERLOGGED);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public static class Convertible implements IPasscodeConvertible {
		private final Block unprotectedBlock, protectedBlock;

		public Convertible(Block unprotectedBlock, Block protectedBlock) {
			this.unprotectedBlock = unprotectedBlock;
			this.protectedBlock = protectedBlock;
		}

		@Override
		public boolean isUnprotectedBlock(BlockState state) {
			return state.is(unprotectedBlock);
		}

		@Override
		public boolean isProtectedBlock(BlockState state) {
			return state.is(protectedBlock);
		}

		@Override
		public boolean protect(PlayerEntity player, World level, BlockPos pos) {
			return convert(player, level, pos, protectedBlock, true);
		}

		@Override
		public boolean unprotect(PlayerEntity player, World level, BlockPos pos) {
			return convert(player, level, pos, unprotectedBlock, false);
		}

		public boolean convert(PlayerEntity player, World level, BlockPos pos, Block convertedBlock, boolean protect) {
			BlockState state = level.getBlockState(pos);
			Direction facing = state.getValue(FACING);
			boolean lit = state.getValue(LIT);
			AbstractFurnaceTileEntity furnace = (AbstractFurnaceTileEntity) level.getBlockEntity(pos);
			CompoundNBT tag;
			BlockState convertedState = convertedBlock.defaultBlockState().setValue(FACING, facing).setValue(LIT, lit);

			if (protect)
				convertedState = convertedState.setValue(OPEN, false);
			else
				((IModuleInventory) furnace).dropAllModules();

			tag = furnace.save(new CompoundNBT());
			furnace.clearContent();
			level.setBlockAndUpdate(pos, convertedState);
			furnace = (AbstractFurnaceTileEntity) level.getBlockEntity(pos);
			furnace.load(null, tag);

			if (protect && player != null)
				((IOwnable) furnace).setOwner(player.getUUID().toString(), player.getName().getString());

			return true;
		}
	}
}
