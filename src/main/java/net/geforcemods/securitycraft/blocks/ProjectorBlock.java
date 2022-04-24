package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.math.shapes.IBooleanFunction;
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
	private static final VoxelShape NORTH = Stream.of(Block.box(12, 0, 12, 15, 2, 15), Block.box(1, 2, 1, 15, 7, 15), Block.box(9, 2, 15, 14, 7, 16), Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 0, 12, 4, 2, 15)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	private static final VoxelShape SOUTH = Stream.of(Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 2, 1, 15, 7, 15), Block.box(2, 2, 0, 7, 7, 1), Block.box(1, 0, 12, 4, 2, 15), Block.box(12, 0, 12, 15, 2, 15), Block.box(12, 0, 1, 15, 2, 4)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	private static final VoxelShape WEST = Stream.of(Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 2, 1, 15, 7, 15), Block.box(15, 2, 2, 16, 7, 7), Block.box(1, 0, 1, 4, 2, 4), Block.box(1, 0, 12, 4, 2, 15), Block.box(12, 0, 12, 15, 2, 15)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	private static final VoxelShape EAST = Stream.of(Block.box(1, 0, 12, 4, 2, 15), Block.box(1, 2, 1, 15, 7, 15), Block.box(0, 2, 9, 1, 7, 14), Block.box(12, 0, 12, 15, 2, 15), Block.box(12, 0, 1, 15, 2, 4), Block.box(1, 0, 1, 4, 2, 4)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());

	public ProjectorBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(world, pos, ctx);
		else {
			switch (disguisedState.getValue(FACING)) {
				case NORTH:
					return NORTH;
				case EAST:
					return EAST;
				case SOUTH:
					return SOUTH;
				case WEST:
					return WEST;
				default:
					return VoxelShapes.block();
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getBlockEntity(pos);

		if (!(te instanceof ProjectorBlockEntity))
			return ActionResultType.FAIL;

		boolean isOwner = ((IOwnable) te).getOwner().isOwner(player);

		if (!world.isClientSide && isOwner)
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);

		return isOwner ? ActionResultType.SUCCESS : ActionResultType.FAIL;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity te = world.getBlockEntity(pos);

		if (te instanceof ProjectorBlockEntity) {
			// Drop the block being projected
			ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), ((ProjectorBlockEntity) te).getStackInSlot(36));

			LevelUtils.addScheduledTask(world, () -> world.addFreshEntity(item));
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide) {
			TileEntity tile = world.getBlockEntity(pos);

			if (tile instanceof ProjectorBlockEntity) {
				ProjectorBlockEntity te = (ProjectorBlockEntity) tile;

				if (te.isActivatedByRedstone()) {
					te.setActive(world.hasNeighborSignal(pos));
					world.sendBlockUpdated(pos, state, state, 3);
				}
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (!world.hasNeighborSignal(pos)) {
			TileEntity tile = world.getBlockEntity(pos);

			if (tile instanceof ProjectorBlockEntity) {
				ProjectorBlockEntity te = (ProjectorBlockEntity) tile;

				if (te.isActivatedByRedstone())
					te.setActive(false);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer) {
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
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
	public void appendHoverText(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(TOOLTIP);
	}
}
