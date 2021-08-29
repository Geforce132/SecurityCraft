package net.geforcemods.securitycraft.blockentities;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMath;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedPistonMovingBlockEntity extends BlockEntity implements IOwnable { //this class doesn't extend PistonBlockEntity because almost all of that class' content is private

	private BlockState movedState;
	private CompoundTag movedBlockEntityTag;
	private Direction direction;
	private boolean extending;
	private boolean isSourcePiston;
	private static final ThreadLocal<Direction> NOCLIP = ThreadLocal.withInitial(() -> null);
	private float progress;
	/** The extension / retraction progress */
	private float lastProgress;
	private long lastTicked;
	private int deathTicks;
	private Owner owner = new Owner();

	public ReinforcedPistonMovingBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.beTypeReinforcedPiston, pos, state);
	}

	public ReinforcedPistonMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, CompoundTag tag, Direction direction, boolean extending, boolean shouldHeadBeRendered) {
		this(pos, state);
		this.movedState = movedState;
		this.movedBlockEntityTag = tag;
		this.direction = direction;
		this.extending = extending;
		this.isSourcePiston = shouldHeadBeRendered;
		this.owner = Owner.fromCompound(tag);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.save(new CompoundTag());
	}

	public boolean isExtending() {
		return extending;
	}

	public Direction getFacing() {
		return direction;
	}

	public boolean isSourcePiston() {
		return isSourcePiston;
	}

	/**
	 * @return interpolated progress value (between lastProgress and progress) given the partialTicks
	 */
	public float getProgress(float ticks) {
		if (ticks > 1.0F) {
			ticks = 1.0F;
		}

		return Mth.lerp(ticks, lastProgress, progress);
	}

	public float getOffsetX(float ticks) {
		return this.direction.getStepX() * getExtendedProgress(getProgress(ticks));
	}

	public float getOffsetY(float ticks) {
		return direction.getStepY() * getExtendedProgress(getProgress(ticks));
	}

	public float getOffsetZ(float ticks) {
		return direction.getStepZ() * getExtendedProgress(getProgress(ticks));
	}

	private float getExtendedProgress(float progress) {
		return extending ? progress - 1.0F : 1.0F - progress;
	}

	private BlockState getCollisionRelatedBlockState() {
		return !isExtending() && isSourcePiston() && movedState.getBlock() instanceof ReinforcedPistonBaseBlock ? SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.SHORT, this.progress > 0.25F).setValue(PistonHeadBlock.TYPE, movedState.is(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT).setValue(PistonHeadBlock.FACING, movedState.getValue(PistonBaseBlock.FACING)) : movedState;
	}

	private static void moveCollidedEntities(Level world, BlockPos pos, float progress, ReinforcedPistonMovingBlockEntity te) {
		Direction direction = te.getMovementDirection();
		double d0 = progress - te.progress;
		VoxelShape collisionShape = te.getCollisionRelatedBlockState().getCollisionShape(world, pos);

		if (!collisionShape.isEmpty()) {
			AABB boundingBox = moveByPositionAndProgress(pos, collisionShape.bounds(), te);
			List<Entity> list = world.getEntities(null, PistonMath.getMovementArea(boundingBox, direction, d0).minmax(boundingBox));

			if (!list.isEmpty()) {
				List<AABB> boundingBoxes = collisionShape.toAabbs();
				boolean isSlimeBlock = te.movedState.isSlimeBlock();
				Iterator<Entity> entities = list.iterator();

				while(true) {
					Entity entity;

					while(true) {
						if (!entities.hasNext()) {
							return;
						}

						entity = entities.next();
						if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
							if (!isSlimeBlock) {
								break;
							}

							if (!(entity instanceof ServerPlayer)) {
								Vec3 vec3 = entity.getDeltaMovement();
								double x = vec3.x;
								double y = vec3.y;
								double z = vec3.z;

								switch (direction.getAxis()) {
									case X -> x = direction.getStepX();
									case Y -> y = direction.getStepY();
									case Z -> z = direction.getStepZ();
								}

								entity.setDeltaMovement(x, y, z);
								break;
							}
						}
					}

					double d4 = 0.0D;

					for(AABB aabb : boundingBoxes) {
						AABB movementArea = PistonMath.getMovementArea(moveByPositionAndProgress(pos, aabb, te), direction, d0);
						AABB entityCollision = entity.getBoundingBox();

						if (movementArea.intersects(entityCollision)) {
							d4 = Math.max(d4, getMovement(movementArea, direction, entityCollision));
							if (d4 >= d0) {
								break;
							}
						}
					}

					if (!(d4 <= 0.0D)) {
						d4 = Math.min(d4, d0) + 0.01D;
						moveEntityByPiston(direction, entity, d4, direction);

						if (!te.extending && te.isSourcePiston) {
							fixEntityWithinPistonBase(pos, entity, direction, d0);
						}
					}
				}
			}
		}
	}

	private static void moveEntityByPiston(Direction direction, Entity entity, double progress, Direction moveDirection) {
		NOCLIP.set(direction);
		entity.move(MoverType.PISTON, new Vec3(progress * moveDirection.getStepX(), progress * moveDirection.getStepY(), progress * moveDirection.getStepZ()));
		NOCLIP.set(null);
	}

	private static void moveStuckEntities(Level world, BlockPos pos, float progress, ReinforcedPistonMovingBlockEntity te) {
		if (te.isStickyForEntities()) {
			Direction direction = te.getMovementDirection();

			if (direction.getAxis().isHorizontal()) {
				double collisionShapeTop = te.movedState.getCollisionShape(world, pos).max(Direction.Axis.Y);
				AABB aabb = moveByPositionAndProgress(pos, new AABB(0.0D, collisionShapeTop, 0.0D, 1.0D, 1.5000000999999998D, 1.0D), te);
				double d1 = progress - te.progress;

				for(Entity entity : world.getEntities((Entity)null, aabb, entity -> matchesStickyCriteria(aabb, entity))) {
					moveEntityByPiston(direction, entity, d1, direction);
				}

			}
		}
	}

	private static boolean matchesStickyCriteria(AABB shape, Entity entity) {
		return entity.getPistonPushReaction() == PushReaction.NORMAL && entity.isOnGround() && entity.getX() >= shape.minX && entity.getX() <= shape.maxX && entity.getZ() >= shape.minZ && entity.getZ() <= shape.maxZ;
	}

	private boolean isStickyForEntities() {
		return this.movedState.is(Blocks.HONEY_BLOCK);
	}

	public Direction getMovementDirection() {
		return extending ? direction : direction.getOpposite();
	}

	private static double getMovement(AABB headShape, Direction direction, AABB facing) {
		return switch (direction) {
			case EAST -> headShape.maxX - facing.minX;
			case WEST -> facing.maxX - headShape.minX;
			case DOWN -> facing.maxY - headShape.minY;
			case SOUTH -> headShape.maxZ - facing.minZ;
			case NORTH -> facing.maxZ - headShape.minZ;
			default -> headShape.maxY - facing.minY;
		};
	}

	private static AABB moveByPositionAndProgress(BlockPos pos, AABB boundingBox, ReinforcedPistonMovingBlockEntity te) {
		double extendedProgress = te.getExtendedProgress(te.progress);
		return boundingBox.move(pos.getX() + extendedProgress * te.direction.getStepX(), pos.getY() + extendedProgress * te.direction.getStepY(), pos.getZ() + extendedProgress * te.direction.getStepZ());
	}

	private static void fixEntityWithinPistonBase(BlockPos pos, Entity entity, Direction pushDirection, double progress) {
		AABB entityBoundingBox = entity.getBoundingBox();
		AABB pistonBoundingBox = Shapes.block().bounds().move(pos);

		if (entityBoundingBox.intersects(pistonBoundingBox)) {
			Direction direction = pushDirection.getOpposite();
			double d0 = getMovement(pistonBoundingBox, direction, entityBoundingBox) + 0.01D;
			double d1 = getMovement(pistonBoundingBox, direction, entityBoundingBox.intersect(pistonBoundingBox)) + 0.01D;

			if (Math.abs(d0 - d1) < 0.01D) {
				d0 = Math.min(d0, progress) + 0.01D;
				moveEntityByPiston(pushDirection, entity, d0, direction);
			}
		}
	}

	public BlockState getMovedState() {
		return movedState;
	}

	/**
	 * Removes the piston's BlockEntity and stops any movement
	 */
	public void finalTick() {
		if (level != null && (lastProgress < 1.0F || level.isClientSide)) {
			progress = 1.0F;
			lastProgress = progress;
			level.removeBlockEntity(worldPosition);
			setRemoved();

			if (level.getBlockState(worldPosition).is(SCContent.REINFORCED_MOVING_PISTON.get())) {
				BlockState pushedState;

				if (isSourcePiston) {
					pushedState = Blocks.AIR.defaultBlockState();
				} else {
					pushedState = Block.updateFromNeighbourShapes(movedState, level, worldPosition);
				}

				if (movedBlockEntityTag != null){
					BlockEntity te = pushedState.hasBlockEntity() ? ((EntityBlock)pushedState.getBlock()).newBlockEntity(worldPosition, pushedState) : null;

					if (te != null){
						te.load(movedBlockEntityTag);
						level.setBlockEntity(te);
					}
				}

				level.setBlock(worldPosition, pushedState, 3);
				level.neighborChanged(worldPosition, pushedState.getBlock(), worldPosition);
			}
		}
	}

	public static void tick(Level world, BlockPos pos, BlockState state, ReinforcedPistonMovingBlockEntity te) {
		te.lastTicked = world.getGameTime();
		te.lastProgress = te.progress;

		if (te.lastProgress >= 1.0F) {
			if (world.isClientSide && te.deathTicks < 5) {
				++te.deathTicks;
			} else {
				world.removeBlockEntity(pos);
				te.setRemoved();

				if (te.movedState != null && world.getBlockState(pos).is(SCContent.REINFORCED_MOVING_PISTON.get())) {
					BlockState pushedState = Block.updateFromNeighbourShapes(te.movedState, world, pos);

					if (pushedState.isAir()) {
						world.setBlock(pos, te.movedState, 84);
						Block.updateOrDestroy(te.movedState, pushedState, world, pos, 3);
					} else {
						if (pushedState.hasProperty(BlockStateProperties.WATERLOGGED) && pushedState.getValue(BlockStateProperties.WATERLOGGED)) {
							pushedState = pushedState.setValue(BlockStateProperties.WATERLOGGED, false);
						}

						if (te.movedBlockEntityTag != null){
							BlockEntity storedTe = pushedState.hasBlockEntity() ? ((EntityBlock)pushedState.getBlock()).newBlockEntity(te.worldPosition, pushedState) : null;

							if (storedTe != null){
								storedTe.load(te.movedBlockEntityTag);
								world.setBlockEntity(storedTe);
							}
						}

						world.setBlock(pos, pushedState, 67);
						world.neighborChanged(pos, pushedState.getBlock(), pos);
					}
				}
			}
		} else {
			float f = te.progress + 0.5F;

			moveCollidedEntities(world, pos, f, te);
			moveStuckEntities(world, pos, f, te);
			te.progress = f;

			if (te.progress >= 1.0F) {
				te.progress = 1.0F;
			}
		}
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		movedState = NbtUtils.readBlockState(compound.getCompound("blockState"));
		direction = Direction.from3DDataValue(compound.getInt("facing"));
		progress = compound.getFloat("progress");
		lastProgress = this.progress;
		extending = compound.getBoolean("extending");
		isSourcePiston = compound.getBoolean("source");
		movedBlockEntityTag = (CompoundTag)compound.get("movedBlockEntityTag");
		owner.load(compound);
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		super.save(compound);
		compound.put("blockState", NbtUtils.writeBlockState(movedState));
		compound.putInt("facing", direction.get3DDataValue());
		compound.putFloat("progress", lastProgress);
		compound.putBoolean("extending", extending);
		compound.putBoolean("source", isSourcePiston);

		if (movedBlockEntityTag != null)
			compound.put("movedBlockEntityTag", movedBlockEntityTag);

		if(owner != null){
			owner.save(movedBlockEntityTag, false);
		}

		return compound;
	}

	public VoxelShape getCollisionShape(BlockGetter world, BlockPos pos) {
		VoxelShape shape;

		if (!extending && isSourcePiston) {
			shape = movedState.setValue(PistonBaseBlock.EXTENDED, true).getCollisionShape(world, pos);
		} else {
			shape = Shapes.empty();
		}

		if (progress < 1.0D && NOCLIP.get() == getMovementDirection()) {
			return shape;
		} else {
			BlockState state;

			if (isSourcePiston()) {
				state = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.FACING, direction).setValue(PistonHeadBlock.SHORT, extending != 1.0F - progress < 4.0F);
			} else {
				state = movedState;
			}

			float progress = getExtendedProgress(this.progress);
			double x = direction.getStepX() * progress;
			double y = direction.getStepY() * progress;
			double z = direction.getStepZ() * progress;

			return Shapes.or(shape, state.getCollisionShape(world, pos).move(x, y, z));
		}
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	public long getLastTicked() {
		return lastTicked;
	}
}
