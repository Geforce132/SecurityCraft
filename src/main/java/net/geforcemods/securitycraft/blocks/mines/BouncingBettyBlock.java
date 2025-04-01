package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BouncingBettyBlock extends ExplosiveBlock implements IWaterLoggable {
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 3, 13);

	public BouncingBettyBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (level.getBlockState(pos.below()).isAir()) {
			if (level.getBlockState(pos).getValue(DEACTIVATED))
				level.destroyBlock(pos, true);
			else
				explode(level, pos);
		}
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		return BlockUtils.isSideSolid(level, pos.below(), Direction.UP);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if (getShape(state, level, pos, ISelectionContext.of(entity)).bounds().move(pos).inflate(0.01D).intersects(entity.getBoundingBox()) && !Utils.doesEntityOwn(entity, level, pos) && !(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) && !((IOwnable) level.getBlockEntity(pos)).allowsOwnableEntity(entity))
			explode(level, pos);
	}

	@Override
	public void attack(BlockState state, World level, BlockPos pos, PlayerEntity player) {
		if (!player.isCreative() && !Utils.doesEntityOwn(player, level, pos))
			explode(level, pos);
	}

	@Override
	public boolean activateMine(World level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(World level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (!state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(World level, BlockPos pos) {
		if (level.isClientSide)
			return;

		if (level.getBlockState(pos).getValue(DEACTIVATED))
			return;

		BouncingBetty bouncingBettyEntity = new BouncingBetty(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);

		level.destroyBlock(pos, false);
		bouncingBettyEntity.setFuse(15);
		bouncingBettyEntity.setDeltaMovement(bouncingBettyEntity.getDeltaMovement().multiply(1, 0, 1).add(0, 0.5D, 0));
		LevelUtils.addScheduledTask(level, () -> level.addFreshEntity(bouncingBettyEntity));
		bouncingBettyEntity.playSound(SoundEvents.TNT_PRIMED, 1.0F, 1.0F);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(DEACTIVATED, WATERLOGGED);
	}

	@Override
	public boolean isActive(World level, BlockPos pos) {
		return !level.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}
}
