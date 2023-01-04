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
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LaserFieldBlock extends OwnableBlock implements IOverlayDisplay {
	public static final IntegerProperty BOUNDTYPE = IntegerProperty.create("boundtype", 1, 3);
	private static final VoxelShape SHAPE_X = Block.box(0, 6.75, 6.75, 16, 9.25, 9.25);
	private static final VoxelShape SHAPE_Y = Block.box(6.75, 0, 6.75, 9.25, 16, 9.25);
	private static final VoxelShape SHAPE_Z = Block.box(6.75, 6.75, 0, 9.25, 9.25, 16);

	public LaserFieldBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(BOUNDTYPE, 1));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return Shapes.empty();
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide && entity instanceof LivingEntity livingEntity && !EntityUtils.isInvisible(livingEntity)) {
			if (!getShape(state, level, pos, CollisionContext.of(entity)).bounds().move(pos).intersects(entity.getBoundingBox()))
				return;

			for (int i = 0; i < ConfigHandler.SERVER.laserBlockRange.get(); i++) {
				BlockPos offsetPos = pos.relative(getFieldDirection(state), i);
				BlockState offsetState = level.getBlockState(offsetPos);
				Block offsetBlock = offsetState.getBlock();

				if (offsetBlock == SCContent.LASER_BLOCK.get()) {
					if (level.getBlockEntity(offsetPos) instanceof LaserBlockBlockEntity laser) {
						if (laser.isAllowed(entity))
							return;

						if (!(entity instanceof Player player && laser.isOwnedBy(player) && laser.ignoresOwner())) {
							if (laser.isModuleEnabled(ModuleType.REDSTONE) && !offsetState.getValue(LaserBlock.POWERED)) {
								level.setBlockAndUpdate(offsetPos, offsetState.setValue(LaserBlock.POWERED, true));
								BlockUtils.updateIndirectNeighbors(level, offsetPos, SCContent.LASER_BLOCK.get());
								level.scheduleTick(offsetPos, SCContent.LASER_BLOCK.get(), 50);
								laser.createLinkedBlockAction(new ILinkedAction.StateChanged(LaserBlock.POWERED), laser);
							}

							if (laser.isModuleEnabled(ModuleType.HARMING)) {
								double damage = ConfigHandler.SERVER.laserDamage.get();

								livingEntity.hurt(CustomDamageSources.LASER, (float) damage);
							}
						}

						break;
					}
				}
			}
		}
	}

	public static Direction getFieldDirection(BlockState state) {
		return switch (state.getValue(BOUNDTYPE)) {
			case 1 -> Direction.UP;
			case 2 -> Direction.SOUTH;
			case 3 -> Direction.EAST;
			default -> null;
		};
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			Direction direction = Direction.from3DDataValue((state.getValue(LaserFieldBlock.BOUNDTYPE) - 1) * 2);

			BlockUtils.destroyInSequence(this, level, pos, direction, direction.getOpposite());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(BOUNDTYPE)) {
			case 1 -> SHAPE_Y;
			case 2 -> SHAPE_Z;
			case 3 -> SHAPE_X;
			default -> Shapes.empty();
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer) {
		return defaultBlockState().setValue(BOUNDTYPE, 1);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BOUNDTYPE);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(SCContent.ABSTRACT_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		int boundType = state.getValue(BOUNDTYPE);

		return rot == Rotation.CLOCKWISE_180 ? state : state.setValue(BOUNDTYPE, boundType == 2 ? 3 : (boundType == 3 ? 2 : 1));
	}

	@Override
	public ItemStack getDisplayStack(Level level, BlockState state, BlockPos pos) {
		return null;
	}

	@Override
	public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
		return false;
	}
}
