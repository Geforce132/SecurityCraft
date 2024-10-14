package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.blockentities.BrushableMineBlockEntity;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class BrushableMineBlock extends FallingBlockMineBlock implements IBlockMine {
	public static final IntegerProperty DUSTED = BlockStateProperties.DUSTED;
	public static final BooleanProperty SAFE = BooleanProperty.create("safe");

	public BrushableMineBlock(BlockBehaviour.Properties properties, Block disguisedBlock) {
		super(properties, disguisedBlock);
		registerDefaultState(stateDefinition.any().setValue(DUSTED, 0).setValue(SAFE, false));
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public boolean isActive(Level level, BlockPos pos) {
		return !level.getBlockState(pos).getValue(SAFE);
	}

	@Override
	public boolean defuseMine(Level level, BlockPos pos) {
		level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(SAFE, true));
		return true;
	}

	@Override
	public boolean activateMine(Level level, BlockPos pos) {
		level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(SAFE, false));
		return true;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof BrushableMineBlockEntity be)
			be.checkReset(level);

		if (FallingBlock.isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinY())
			FallingBlockEntity.fall(level, pos, state).disableDrop();
	}

	@Override
	public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity be) {
		Vec3 vec3 = be.getBoundingBox().getCenter();

		level.levelEvent(2001, BlockPos.containing(vec3), Block.getId(be.getBlockState()));
		level.gameEvent(be, GameEvent.BLOCK_DESTROY, vec3);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BrushableMineBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DUSTED, SAFE);
	}

	@Override
	public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, BlockState queryState, BlockPos queryPos) {
		return getBlockDisguisedAs().defaultBlockState().setValue(DUSTED, state.getValue(DUSTED));
	}

	public Block getTurnsInto() {
		return ((BrushableBlock) getBlockDisguisedAs()).getTurnsInto();
	}
}
