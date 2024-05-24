package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LaserFieldBlock extends OwnableBlock implements IOverlayDisplay, IWaterLoggable {
	public static final IntegerProperty BOUNDTYPE = IntegerProperty.create("boundtype", 1, 3);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE_X = Block.box(0, 6.75, 6.75, 16, 9.25, 9.25);
	private static final VoxelShape SHAPE_Y = Block.box(6.75, 0, 6.75, 9.25, 16, 9.25);
	private static final VoxelShape SHAPE_Z = Block.box(6.75, 6.75, 0, 9.25, 9.25, 16);

	public LaserFieldBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BOUNDTYPE, 1).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		return VoxelShapes.empty();
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
		if (!level.isClientSide && entity instanceof LivingEntity) {
			if (!getShape(state, level, pos, ISelectionContext.of(entity)).bounds().move(pos).intersects(entity.getBoundingBox()))
				return;

			for (int i = 0; i < ConfigHandler.SERVER.laserBlockRange.get(); i++) {
				BlockPos offsetPos = pos.relative(getFieldDirection(state), i);
				BlockState offsetState = level.getBlockState(offsetPos);
				Block offsetBlock = offsetState.getBlock();

				if (offsetBlock == SCContent.LASER_BLOCK.get()) {
					TileEntity te = level.getBlockEntity(offsetPos);

					if (te instanceof LaserBlockBlockEntity) {
						LaserBlockBlockEntity laser = (LaserBlockBlockEntity) te;

						if (laser.isAllowed(entity) || laser.isConsideredInvisible((LivingEntity) entity))
							return;

						if (!(entity instanceof PlayerEntity && laser.isOwnedBy(entity) && laser.ignoresOwner())) {
							if (laser.allowsOwnableEntity(entity))
								return;

							if (laser.isModuleEnabled(ModuleType.REDSTONE)) {
								if (laser.timeSinceLastToggle() < 500)
									laser.setLastToggleTime(System.currentTimeMillis());
								else {
									int signalLength = laser.getSignalLength();
									boolean wasPowered = offsetState.getValue(LaserBlock.POWERED);

									laser.setLastToggleTime(System.currentTimeMillis());
									level.setBlockAndUpdate(offsetPos, offsetState.cycle(LaserBlock.POWERED));
									BlockUtils.updateIndirectNeighbors(level, offsetPos, SCContent.LASER_BLOCK.get());
									laser.propagate(new ILinkedAction.StateChanged<>(LaserBlock.POWERED, wasPowered, !wasPowered), laser);

									if (signalLength > 0)
										level.getBlockTicks().scheduleTick(offsetPos, SCContent.LASER_BLOCK.get(), signalLength);
								}
							}

							if (laser.isModuleEnabled(ModuleType.HARMING)) {
								double damage = ConfigHandler.SERVER.laserDamage.get();

								((LivingEntity) entity).hurt(CustomDamageSources.LASER, (float) damage);
							}
						}

						break;
					}
				}
			}
		}
	}

	public static Direction getFieldDirection(BlockState state) {
		if (state.getBlock() instanceof LaserFieldBlock) {
			int boundType = state.getValue(BOUNDTYPE);

			if (boundType == 1)
				return Direction.UP;
			else if (boundType == 2)
				return Direction.SOUTH;
			else if (boundType == 3)
				return Direction.EAST;
		}

		return null;
	}

	public static int getBoundType(Direction direction) {
		switch (direction) {
			case UP:
			case DOWN:
				return 1;
			case NORTH:
			case SOUTH:
				return 2;
			case EAST:
			case WEST:
				return 3;
			default:
				return 1;
		}
	}

	@Override
	public void destroy(IWorld level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			int boundType = state.getValue(LaserFieldBlock.BOUNDTYPE);
			Direction direction = Direction.from3DDataValue((boundType - 1) * 2);

			BlockUtils.removeInSequence((directionToCheck, stateToCheck) -> stateToCheck.getBlock() == this && stateToCheck.getValue(BOUNDTYPE) == boundType, level, pos, direction, direction.getOpposite());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		if (level.getBlockState(pos).getBlock() instanceof LaserFieldBlock) {
			int boundType = level.getBlockState(pos).getValue(BOUNDTYPE);

			if (boundType == 1)
				return SHAPE_Y;
			else if (boundType == 2)
				return SHAPE_Z;
			else if (boundType == 3)
				return SHAPE_X;
		}

		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return getPotentiallyWaterloggedState(1, ctx.getLevel(), ctx.getClickedPos());
	}

	public BlockState getPotentiallyWaterloggedState(int boundType, World level, BlockPos pos) {
		return defaultBlockState().setValue(BOUNDTYPE, boundType).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BOUNDTYPE, WATERLOGGED);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		int boundType = state.getValue(BOUNDTYPE);

		return rot == Rotation.CLOCKWISE_180 ? state : state.setValue(BOUNDTYPE, boundType == 2 ? 3 : (boundType == 3 ? 2 : 1));
	}

	@Override
	public ItemStack getDisplayStack(World level, BlockState state, BlockPos pos) {
		return null;
	}

	@Override
	public boolean shouldShowSCInfo(World level, BlockState state, BlockPos pos) {
		return false;
	}
}
