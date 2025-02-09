package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SometimesVisibleBlock extends DisguisableBlock {
	public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");

	public SometimesVisibleBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(INVISIBLE, true).setValue(WATERLOGGED, false));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState pState) {
		return PushReaction.BLOCK;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader level, BlockPos pos) {
		return state.getValue(INVISIBLE);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		return state.getValue(INVISIBLE) ? VoxelShapes.empty() : super.getShape(state, level, pos, ctx);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return state.getValue(INVISIBLE) ? BlockRenderType.INVISIBLE : super.getRenderShape(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		return state.getValue(INVISIBLE) ? getCollisionShapeWhenInvisible() : super.getCollisionShape(state, level, pos, ctx);
	}

	public VoxelShape getCollisionShapeWhenInvisible() {
		return VoxelShapes.block();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader level, BlockPos pos) {
		return state.getValue(INVISIBLE) ? VoxelShapes.empty() : super.getOcclusionShape(state, level, pos);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new DisguisableBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(INVISIBLE, WATERLOGGED);
	}
}
