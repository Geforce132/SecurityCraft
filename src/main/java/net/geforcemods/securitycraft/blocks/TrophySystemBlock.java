package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TrophySystemBlock extends DisguisableBlock {
	//@formatter:off
	private static final VoxelShape SHAPE = Stream.of(
			VoxelShapes.box(0.3125, 0.625, 0.3125, 0.6875, 1, 0.6875),
			VoxelShapes.box(0.375, 0.375, 0.375, 0.625, 0.625, 0.625),
			VoxelShapes.box(0.4375, 0.4375, 0.625, 0.5625, 0.5625, 0.875),
			VoxelShapes.box(0.375, 0.5, 0.8125, 0.625, 0.75, 1),
			VoxelShapes.box(0, 0.5, 0.375, 0.1875, 0.75, 0.625),
			VoxelShapes.box(0.625, 0.4375, 0.4375, 0.875, 0.5625, 0.5625),
			VoxelShapes.box(0.125, 0.4375, 0.4375, 0.375, 0.5625, 0.5625),
			VoxelShapes.box(0.8125, 0.5, 0.375, 1, 0.75, 0.625),
			VoxelShapes.box(0.4375, 0.4375, 0.125, 0.5625, 0.5625, 0.375),
			VoxelShapes.box(0.375, 0.5, 0, 0.625, 0.75, 0.1875),
			VoxelShapes.box(0.4375, 0.1875, 0.4375, 0.5625, 0.375, 0.5625)
	).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	//@formatter:on

	public TrophySystemBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		return BlockUtils.isSideSolid(level, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!canSurvive(state, level, pos))
			level.destroyBlock(pos, true);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof TrophySystemBlockEntity) {
			TrophySystemBlockEntity be = (TrophySystemBlockEntity) te;

			if (be.isOwnedBy(player)) {
				if (!level.isClientSide) {
					if (be.isDisabled())
						player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else
						NetworkHooks.openGui((ServerPlayerEntity) player, be, pos);
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedStateOrDefault(state, level, pos);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return SHAPE;
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof TrophySystemBlockEntity)
				((TrophySystemBlockEntity) te).getInventory().clear();
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof TrophySystemBlockEntity) {
				InventoryHelper.dropContents(level, pos, ((TrophySystemBlockEntity) te).getLensContainer());
				((TrophySystemBlockEntity) te).dropAllModules();
				level.updateNeighbourForOutputSignal(pos, this);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new TrophySystemBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
}
