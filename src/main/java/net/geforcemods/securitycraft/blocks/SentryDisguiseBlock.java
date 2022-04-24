package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SentryDisguiseBlock extends DisguisableBlock {
	public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");

	public SentryDisguiseBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(INVISIBLE, true));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
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
		return state.getValue(INVISIBLE) ? VoxelShapes.block() : super.getCollisionShape(state, level, pos, ctx);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, IBlockReader level, BlockPos pos) {
		return state.getValue(INVISIBLE) ? VoxelShapes.empty() : super.getOcclusionShape(state, level, pos);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new DisguisableBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(INVISIBLE);
	}
}
