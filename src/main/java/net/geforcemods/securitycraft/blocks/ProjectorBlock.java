package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class ProjectorBlock extends DisguisableBlock {
	private static final IFormattableTextComponent TOOLTIP = new TranslationTextComponent("tooltip.securitycraft:projector").setStyle(Utils.GRAY_STYLE);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
	private static final VoxelShape FLOOR_NORTH = VoxelShapes.or(Block.box(12, 0, 12, 15, 2, 15), Block.box(1, 2, 1, 15, 7, 15), Block.box(9, 2, 15, 14, 7, 16), Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 0, 12, 4, 2, 15));
	private static final VoxelShape FLOOR_SOUTH = VoxelShapes.or(Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 2, 1, 15, 7, 15), Block.box(2, 2, 0, 7, 7, 1), Block.box(1, 0, 12, 4, 2, 15), Block.box(12, 0, 12, 15, 2, 15), Block.box(12, 0, 1, 15, 2, 4));
	private static final VoxelShape FLOOR_WEST = VoxelShapes.or(Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 2, 1, 15, 7, 15), Block.box(15, 2, 2, 16, 7, 7), Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 0, 12, 4, 2, 15), Block.box(12, 0, 12, 15, 2, 15));
	private static final VoxelShape FLOOR_EAST = VoxelShapes.or(Block.box(1, 0, 12, 4, 2, 15), Block.box(1, 2, 1, 15, 7, 15), Block.box(0, 2, 9, 1, 7, 14), Block.box(12, 0, 12, 15, 2, 15), Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 0, 1, 4, 2, 4));
	private static final VoxelShape CEILING_NORTH = VoxelShapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(9, 6, 15, 14, 11, 16), Block.box(7, 11, 4, 9, 16, 6));
	private static final VoxelShape CEILING_SOUTH = VoxelShapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(2, 6, 0, 7, 11, 1), Block.box(7, 11, 10, 9, 16, 12));
	private static final VoxelShape CEILING_WEST = VoxelShapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(15, 6, 2, 16, 11, 7), Block.box(4, 11, 7, 6, 16, 9));
	private static final VoxelShape CEILING_EAST = VoxelShapes.or(Block.box(1, 6, 1, 15, 11, 15), Block.box(0, 6, 9, 1, 11, 14), Block.box(10, 11, 7, 12, 16, 9));

	public ProjectorBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HANGING, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else if (!disguisedState.getValue(HANGING)) {
			switch (disguisedState.getValue(FACING)) {
				case NORTH:
					return FLOOR_NORTH;
				case EAST:
					return FLOOR_EAST;
				case SOUTH:
					return FLOOR_SOUTH;
				case WEST:
					return FLOOR_WEST;
				default:
					return VoxelShapes.block();
			}
		}
		else {
			switch (disguisedState.getValue(FACING)) {
				case NORTH:
					return CEILING_NORTH;
				case EAST:
					return CEILING_EAST;
				case SOUTH:
					return CEILING_SOUTH;
				case WEST:
					return CEILING_WEST;
				default:
					return VoxelShapes.block();
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = level.getBlockEntity(pos);

		if (!(te instanceof ProjectorBlockEntity))
			return ActionResultType.FAIL;

		boolean isOwner = ((IOwnable) te).isOwnedBy(player);

		if (!level.isClientSide && isOwner)
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);

		return isOwner ? ActionResultType.SUCCESS : ActionResultType.FAIL;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
		return direction != null && direction.getAxis() != Axis.Y;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ProjectorBlockEntity)
				LevelUtils.addScheduledTask(level, () -> level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), ((ProjectorBlockEntity) te).getStackInSlot(36))));
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.isClientSide) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ProjectorBlockEntity) {
				ProjectorBlockEntity be = (ProjectorBlockEntity) te;

				if (be.isActivatedByRedstone()) {
					be.setActive(level.hasNeighborSignal(pos));
					level.sendBlockUpdated(pos, state, state, 3);
				}
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random rand) {
		if (!level.hasNeighborSignal(pos)) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ProjectorBlockEntity) {
				ProjectorBlockEntity be = (ProjectorBlockEntity) te;

				if (be.isActivatedByRedstone())
					be.setActive(false);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getPlayer().getDirection().getOpposite()).setValue(HANGING, ctx.getClickedFace() == Direction.DOWN);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, HANGING, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new ProjectorBlockEntity();
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
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, IBlockReader level, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(TOOLTIP);
	}
}
