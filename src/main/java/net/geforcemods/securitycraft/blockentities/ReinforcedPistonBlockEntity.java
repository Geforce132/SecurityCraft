package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AabbHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.Chunk;

public class ReinforcedPistonBlockEntity extends TileEntity implements ITickableTileEntity, IOwnable { //this class doesn't extend PistonTileEntity because almost all of that class' content is private
	public static final List<TileEntity> SCHEDULED_TICKING_BLOCK_ENTITIES = new ArrayList<>();
	private BlockState movedState;
	private CompoundNBT movedBlockEntityTag;
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

	public ReinforcedPistonBlockEntity() {
		super(SCContent.REINFORCED_PISTON_BLOCK_ENTITY.get());
	}

	public ReinforcedPistonBlockEntity(BlockState movedState, CompoundNBT tag, Direction direction, boolean extending, boolean shouldHeadBeRendered) {
		super(SCContent.REINFORCED_PISTON_BLOCK_ENTITY.get());
		this.movedState = movedState;
		this.movedBlockEntityTag = tag;
		this.direction = direction;
		this.extending = extending;
		this.isSourcePiston = shouldHeadBeRendered;
		this.owner = Owner.fromCompound(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
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
		if (ticks > 1.0F)
			ticks = 1.0F;

		return MathHelper.lerp(ticks, lastProgress, progress);
	}

	public float getOffsetX(float ticks) {
		return direction.getStepX() * getExtendedProgress(getProgress(ticks));
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
		return !isExtending() && isSourcePiston() && movedState.getBlock() instanceof ReinforcedPistonBlock ? SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.TYPE, movedState.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get() ? PistonType.STICKY : PistonType.DEFAULT).setValue(DirectionalBlock.FACING, movedState.getValue(DirectionalBlock.FACING)) : movedState;
	}

	private void moveCollidedEntities(float progress) {
		Direction direction = getMovementDirection();
		double progressChange = progress - this.progress;
		VoxelShape collisionShape = getCollisionRelatedBlockState().getCollisionShape(level, getBlockPos());

		if (!collisionShape.isEmpty()) {
			AxisAlignedBB boundingBox = moveByPositionAndProgress(collisionShape.bounds());
			List<Entity> list = level.getEntities((Entity) null, AabbHelper.getMovementArea(boundingBox, direction, progressChange).minmax(boundingBox));

			if (!list.isEmpty()) {
				List<AxisAlignedBB> boundingBoxes = collisionShape.toAabbs();
				boolean isSlimeBlock = movedState.isSlimeBlock();
				Iterator<Entity> entities = list.iterator();

				while (true) {
					Entity entity;

					while (true) {
						if (!entities.hasNext())
							return;

						entity = entities.next();

						if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
							if (!isSlimeBlock)
								break;

							if (!(entity instanceof ServerPlayerEntity)) {
								Vector3d vec3 = entity.getDeltaMovement();
								double x = vec3.x;
								double y = vec3.y;
								double z = vec3.z;

								switch (direction.getAxis()) {
									case X:
										x = direction.getStepX();
										break;
									case Y:
										y = direction.getStepY();
										break;
									case Z:
										z = direction.getStepZ();
								}

								entity.setDeltaMovement(x, y, z);
								break;
							}
						}
					}

					double movement = 0.0D;

					for (AxisAlignedBB aabb : boundingBoxes) {
						AxisAlignedBB movementArea = AabbHelper.getMovementArea(moveByPositionAndProgress(aabb), direction, progressChange);
						AxisAlignedBB entityCollision = entity.getBoundingBox();

						if (movementArea.intersects(entityCollision)) {
							movement = Math.max(movement, getMovement(movementArea, direction, entityCollision));

							if (movement >= progressChange)
								break;
						}
					}

					if (movement > 0.0D) {
						movement = Math.min(movement, progressChange) + 0.01D;
						moveEntityByPiston(direction, entity, movement, direction);

						if (!extending && isSourcePiston)
							fixEntityWithinPistonBase(entity, direction, progressChange);
					}
				}
			}
		}
	}

	private static void moveEntityByPiston(Direction direction, Entity entity, double progress, Direction moveDirection) {
		NOCLIP.set(direction);
		entity.move(MoverType.PISTON, new Vector3d(progress * moveDirection.getStepX(), progress * moveDirection.getStepY(), progress * moveDirection.getStepZ()));
		NOCLIP.set(null);
	}

	private void moveStuckEntities(float progress) {
		if (isStickyForEntities()) {
			Direction direction = getMovementDirection();

			if (direction.getAxis().isHorizontal()) {
				double collisionShapeTop = movedState.getCollisionShape(level, worldPosition).max(Direction.Axis.Y);
				AxisAlignedBB aabb = moveByPositionAndProgress(new AxisAlignedBB(0.0D, collisionShapeTop, 0.0D, 1.0D, 1.5000000999999998D, 1.0D));
				double progressChange = progress - this.progress;

				for (Entity entity : level.getEntities((Entity) null, aabb, entity -> matchesStickyCriteria(aabb, entity))) {
					moveEntityByPiston(direction, entity, progressChange, direction);
				}
			}
		}
	}

	private static boolean matchesStickyCriteria(AxisAlignedBB shape, Entity entity) {
		return entity.getPistonPushReaction() == PushReaction.NORMAL && entity.isOnGround() && entity.getX() >= shape.minX && entity.getX() <= shape.maxX && entity.getZ() >= shape.minZ && entity.getZ() <= shape.maxZ;
	}

	private boolean isStickyForEntities() {
		return movedState.is(Blocks.HONEY_BLOCK);
	}

	public Direction getMovementDirection() {
		return extending ? direction : direction.getOpposite();
	}

	private static double getMovement(AxisAlignedBB headShape, Direction direction, AxisAlignedBB facing) {
		switch (direction) {
			case EAST:
				return headShape.maxX - facing.minX;
			case WEST:
				return facing.maxX - headShape.minX;
			case DOWN:
				return facing.maxY - headShape.minY;
			case SOUTH:
				return headShape.maxZ - facing.minZ;
			case NORTH:
				return facing.maxZ - headShape.minZ;
			case UP:
			default:
				return headShape.maxY - facing.minY;
		}
	}

	private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB boundingBox) {
		double extendedProgress = getExtendedProgress(progress);
		return boundingBox.move(worldPosition.getX() + extendedProgress * direction.getStepX(), worldPosition.getY() + extendedProgress * direction.getStepY(), worldPosition.getZ() + extendedProgress * direction.getStepZ());
	}

	private void fixEntityWithinPistonBase(Entity entity, Direction pushDirection, double progress) {
		AxisAlignedBB entityBoundingBox = entity.getBoundingBox();
		AxisAlignedBB pistonBoundingBox = VoxelShapes.block().bounds().move(worldPosition);

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

	public BlockState getPistonState() {
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

				if (isSourcePiston)
					pushedState = Blocks.AIR.defaultBlockState();
				else
					pushedState = Block.updateFromNeighbourShapes(movedState, level, worldPosition);

				if (movedBlockEntityTag != null) {
					TileEntity be = pushedState.hasTileEntity() ? pushedState.createTileEntity(level) : null;

					if (be != null) {
						Chunk chunk = level.getChunkAt(worldPosition);

						be.load(movedState, movedBlockEntityTag);
						chunk.setBlockEntity(worldPosition, be);
					}
				}

				level.setBlock(worldPosition, pushedState, 3);
				level.neighborChanged(worldPosition, pushedState.getBlock(), worldPosition);
			}
		}
	}

	@Override
	public void tick() {
		lastTicked = level.getGameTime();
		lastProgress = progress;

		if (lastProgress >= 1.0F) {
			if (level.isClientSide && deathTicks < 5)
				++deathTicks;
			else {
				level.removeBlockEntity(worldPosition);
				setRemoved();

				if (movedState != null && level.getBlockState(worldPosition).is(SCContent.REINFORCED_MOVING_PISTON.get())) {
					BlockState pushedState = Block.updateFromNeighbourShapes(movedState, level, worldPosition);

					if (pushedState.isAir()) {
						level.setBlock(worldPosition, movedState, 84);
						Block.updateOrDestroy(movedState, pushedState, level, worldPosition, 3);
					}
					else {
						if (pushedState.hasProperty(BlockStateProperties.WATERLOGGED) && pushedState.getValue(BlockStateProperties.WATERLOGGED))
							pushedState = pushedState.setValue(BlockStateProperties.WATERLOGGED, false);

						if (movedBlockEntityTag != null) {
							TileEntity storedBe = pushedState.hasTileEntity() ? pushedState.createTileEntity(level) : null;

							if (storedBe != null) {
								Chunk chunk = level.getChunkAt(worldPosition);

								storedBe.load(movedState, movedBlockEntityTag);
								chunk.setBlockEntity(worldPosition, storedBe);

								if (storedBe instanceof ITickableTileEntity)
									SCHEDULED_TICKING_BLOCK_ENTITIES.add(storedBe);
							}
						}

						level.setBlock(worldPosition, pushedState, 67);
						level.neighborChanged(worldPosition, pushedState.getBlock(), worldPosition);
					}
				}
			}
		}
		else {
			float f = progress + 0.5F;

			moveCollidedEntities(f);
			moveStuckEntities(f);
			progress = f;

			if (progress >= 1.0F)
				progress = 1.0F;
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		movedState = NBTUtil.readBlockState(compound.getCompound("blockState"));
		direction = Direction.from3DDataValue(compound.getInt("facing"));
		progress = compound.getFloat("progress");
		lastProgress = progress;
		extending = compound.getBoolean("extending");
		isSourcePiston = compound.getBoolean("source");
		movedBlockEntityTag = compound.getCompound("movedTileEntityTag");
		owner.load(compound);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);
		compound.put("blockState", NBTUtil.writeBlockState(movedState));
		compound.putInt("facing", direction.get3DDataValue());
		compound.putFloat("progress", lastProgress);
		compound.putBoolean("extending", extending);
		compound.putBoolean("source", isSourcePiston);
		compound.put("movedTileEntityTag", movedBlockEntityTag);

		if (owner != null)
			owner.save(compound, needsValidation());

		return compound;
	}

	public VoxelShape getCollisionShape(IBlockReader level, BlockPos pos) {
		VoxelShape shape;

		if (!extending && isSourcePiston)
			shape = movedState.setValue(PistonBlock.EXTENDED, true).getBlockSupportShape(level, pos);
		else
			shape = VoxelShapes.empty();

		if (progress < 1.0D && NOCLIP.get() == getMovementDirection())
			return shape;
		else {
			BlockState state;

			if (isSourcePiston())
				state = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(DirectionalBlock.FACING, direction).setValue(PistonHeadBlock.SHORT, extending != 1.0F - progress < 4.0F);
			else
				state = movedState;

			float extendedProgress = getExtendedProgress(progress);
			double x = direction.getStepX() * extendedProgress;
			double y = direction.getStepY() * extendedProgress;
			double z = direction.getStepZ() * extendedProgress;

			return VoxelShapes.or(shape, state.getBlockSupportShape(level, pos).move(x, y, z));
		}
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	public long getLastTicked() {
		return lastTicked;
	}

	@Override
	public double getViewDistance() {
		return 68.0D;
	}
}
