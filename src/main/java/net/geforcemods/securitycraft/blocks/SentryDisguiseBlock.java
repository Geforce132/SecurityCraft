package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(INVISIBLE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return state.getValue(INVISIBLE) ? Shapes.empty() : Shapes.block();
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return state.getValue(INVISIBLE) ? RenderShape.INVISIBLE : super.getRenderShape(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(INVISIBLE) ? Shapes.empty() : Shapes.block();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DisguisableBlockEntity(SCContent.DISGUISABLE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(INVISIBLE);
	}
}
