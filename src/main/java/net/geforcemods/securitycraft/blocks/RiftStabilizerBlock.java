package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

public class RiftStabilizerBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	private static final VoxelShape SHAPE_LOWER = VoxelShapes.or(Block.box(0, 0, 0, 16, 12, 16), Block.box(2, 0, 2, 14, 16, 14));
	private static final VoxelShape SHAPE_UPPER = VoxelShapes.or(Block.box(2, 0, 2, 14, 5, 14), Block.box(4, 0, 4, 12, 8, 12));

	public RiftStabilizerBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, this);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof RiftStabilizerBlockEntity) {
			RiftStabilizerBlockEntity be = ((RiftStabilizerBlockEntity) te);

			if (be.isOwnedBy(player)) {
				if (!level.isClientSide) {
					if (be.isDisabled())
						player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.RIFT_STABILIZER, pos));
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockPos pos = ctx.getClickedPos();
		World level = ctx.getLevel();

		return pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(ctx) ? super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()) : null;
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		BlockPos posAbove = pos.above();

		level.setBlockAndUpdate(posAbove, defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER));

		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, posAbove, ((PlayerEntity) placer)));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf half = state.getValue(HALF);

		if (facing.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP))
			return facingState.is(this) && facingState.getValue(HALF) != half ? state.setValue(POWERED, facingState.getValue(POWERED)) : Blocks.AIR.defaultBlockState();
		else
			return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_LOWER : SHAPE_UPPER;
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!level.isClientSide) {
			if (player.isCreative())
				DoublePlantBlock.preventCreativeDropFromBottomPart(level, pos, state, player);
			else
				dropResources(state, level, pos, null, player, player.getMainHandItem());
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void playerDestroy(World level, PlayerEntity player, BlockPos pos, BlockState state, TileEntity be, ItemStack stack) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), be, stack);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		TileEntity be = level.getBlockEntity(pos);

		return state.getValue(POWERED) && state.getValue(HALF) == DoubleBlockHalf.LOWER && be instanceof RiftStabilizerBlockEntity ? 15 - (int) ((RiftStabilizerBlockEntity) be).getLastTeleportDistance() : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return getSignal(state, level, pos, side);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		if (state.getValue(HALF) == DoubleBlockHalf.LOWER && te instanceof RiftStabilizerBlockEntity) {
			RiftStabilizerBlockEntity be = ((RiftStabilizerBlockEntity) te);

			return be.isModuleEnabled(ModuleType.REDSTONE) && be.getLastTeleportationType() != null ? be.getComparatorOutputFunction().applyAsInt(be.getLastTeleportationType()) : 0;
		}

		return 0;
	}

	public static RiftStabilizerBlockEntity getConnectedBlockEntity(World level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		BlockPos connectedPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
		TileEntity be = level.getBlockEntity(connectedPos);

		return be instanceof RiftStabilizerBlockEntity ? ((RiftStabilizerBlockEntity) be) : null;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF, POWERED, WATERLOGGED);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		BlockPos lowerPos = pos.below();
		BlockState lowerState = level.getBlockState(lowerPos);

		return state.getValue(HALF) == DoubleBlockHalf.LOWER || lowerState.is(this);
	}

	@Override
	public TileEntity createTileEntity(BlockState pos, IBlockReader level) {
		return new RiftStabilizerBlockEntity();
	}
}
