package net.geforcemods.securitycraft.blockentities;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMath;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedPistonMovingBlockEntity extends BlockEntity implements IOwnable { //this class doesn't extend PistonBlockEntity because almost all of that class' content is private
	private static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
	private BlockState movedState = DEFAULT_BLOCK_STATE;
	private BlockEntity beToMove;
	private ValueInput movedBlockEntityTag;
	private Direction direction;
	private boolean extending = false;
	private boolean isSourcePiston = false;
	private static final ThreadLocal<Direction> NOCLIP = ThreadLocal.withInitial(() -> null);
	private float progress = 0.0F;
	/** The extension / retraction progress */
	private float lastProgress = 0.0F;
	private long lastTicked;
	private int deathTicks;
	private Owner owner = new Owner();

	public ReinforcedPistonMovingBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.REINFORCED_PISTON_BLOCK_ENTITY.get(), pos, state);
	}

	public ReinforcedPistonMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, BlockEntity beToMove, Direction direction, boolean extending, boolean shouldHeadBeRendered) {
		this(pos, state);
		this.movedState = movedState;
		this.direction = direction;
		this.extending = extending;
		this.isSourcePiston = shouldHeadBeRendered;
		this.beToMove = beToMove;

		try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(beToMove.problemPath(), SecurityCraft.LOGGER)) {
			HolderLookup.Provider registryAccess = beToMove.getLevel().registryAccess();
			TagValueOutput valueOutput = (TagValueOutput) saveBeToMove(TagValueOutput.createWithContext(problemReporter, registryAccess));
			ValueInput valueInput;
			CompoundTag tag;

			tag = valueOutput.buildResult();
			this.owner = Owner.fromCompound(tag);
			valueInput = TagValueInput.create(problemReporter, registryAccess, tag);
		}
	}

	private ValueOutput saveBeToMove(ValueOutput tag) {
		beToMove.saveWithoutMetadata(tag);
		tag.putInt("x", getBlockPos().getX());
		tag.putInt("y", getBlockPos().getY());
		tag.putInt("z", getBlockPos().getZ());
		return tag;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		return saveCustomOnly(lookupProvider);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
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

		return Mth.lerp(ticks, lastProgress, progress);
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
		return !isExtending() && isSourcePiston() && movedState.getBlock() instanceof ReinforcedPistonBaseBlock ? SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.SHORT, progress > 0.25F).setValue(PistonHeadBlock.TYPE, movedState.is(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT).setValue(DirectionalBlock.FACING, movedState.getValue(DirectionalBlock.FACING)) : movedState;
	}

	private static void moveCollidedEntities(Level level, BlockPos pos, float progress, ReinforcedPistonMovingBlockEntity be) {
		Direction direction = be.getMovementDirection();
		double progressChange = progress - be.progress;
		VoxelShape collisionShape = be.getCollisionRelatedBlockState().getCollisionShape(level, pos);

		if (!collisionShape.isEmpty()) {
			AABB boundingBox = moveByPositionAndProgress(pos, collisionShape.bounds(), be);
			List<Entity> list = level.getEntities(null, PistonMath.getMovementArea(boundingBox, direction, progressChange).minmax(boundingBox));

			if (!list.isEmpty()) {
				List<AABB> boundingBoxes = collisionShape.toAabbs();
				boolean isSlimeBlock = be.movedState.isSlimeBlock();
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

					for (AABB aabb : boundingBoxes) {
						AABB movementArea = PistonMath.getMovementArea(moveByPositionAndProgress(pos, aabb, be), direction, progressChange);
						AABB entityCollision = entity.getBoundingBox();

						if (movementArea.intersects(entityCollision)) {
							d4 = Math.max(d4, getMovement(movementArea, direction, entityCollision));

							if (d4 >= progressChange)
								break;
						}
					}

					if (d4 > 0.0D) {
						d4 = Math.min(d4, progressChange) + 0.01D;
						moveEntityByPiston(direction, entity, d4, direction);

						if (!be.extending && be.isSourcePiston)
							fixEntityWithinPistonBase(pos, entity, direction, progressChange);
					}
				}
			}
		}
	}

	private static void moveEntityByPiston(Direction direction, Entity entity, double progress, Direction moveDirection) {
		Vec3 originalPos = entity.position();

		NOCLIP.set(direction);
		entity.move(MoverType.PISTON, new Vec3(progress * moveDirection.getStepX(), progress * moveDirection.getStepY(), progress * moveDirection.getStepZ()));
		entity.applyEffectsFromBlocks(originalPos, entity.position());
		entity.removeLatestMovementRecording();
		NOCLIP.set(null);
	}

	private static void moveStuckEntities(Level level, BlockPos pos, float progress, ReinforcedPistonMovingBlockEntity be) {
		if (be.isStickyForEntities()) {
			Direction direction = be.getMovementDirection();

			if (direction.getAxis().isHorizontal()) {
				double collisionShapeTop = be.movedState.getCollisionShape(level, pos).max(Direction.Axis.Y);
				AABB aabb = moveByPositionAndProgress(pos, new AABB(0.0D, collisionShapeTop, 0.0D, 1.0D, 1.5000000999999998D, 1.0D), be);
				double progressChange = progress - be.progress;

				for (Entity entity : level.getEntities((Entity) null, aabb, entity -> matchesStickyCriteria(aabb, entity))) {
					moveEntityByPiston(direction, entity, progressChange, direction);
				}
			}
		}
	}

	private static boolean matchesStickyCriteria(AABB shape, Entity entity) {
		return entity.getPistonPushReaction() == PushReaction.NORMAL && entity.onGround() && entity.getX() >= shape.minX && entity.getX() <= shape.maxX && entity.getZ() >= shape.minZ && entity.getZ() <= shape.maxZ;
	}

	private boolean isStickyForEntities() {
		return movedState.is(Blocks.HONEY_BLOCK);
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

	private static AABB moveByPositionAndProgress(BlockPos pos, AABB boundingBox, ReinforcedPistonMovingBlockEntity be) {
		double extendedProgress = be.getExtendedProgress(be.progress);
		return boundingBox.move(pos.getX() + extendedProgress * be.direction.getStepX(), pos.getY() + extendedProgress * be.direction.getStepY(), pos.getZ() + extendedProgress * be.direction.getStepZ());
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

				if (isSourcePiston)
					pushedState = Blocks.AIR.defaultBlockState();
				else
					pushedState = Block.updateFromNeighbourShapes(movedState, level, worldPosition);

				level.setBlock(worldPosition, pushedState, 3);

				if (movedBlockEntityTag != null) { //Through Level#setBlock, any previously existing BE at the given position is cleared, so the actual BE with the necessary data needs to be set afterwards
					BlockEntity be = pushedState.hasBlockEntity() ? ((EntityBlock) pushedState.getBlock()).newBlockEntity(worldPosition, pushedState) : null;

					if (be != null) {
						be.loadWithComponents(movedBlockEntityTag);
						level.setBlockEntity(be);

						if (be instanceof IModuleInventory moduleInv) {
							moduleInv.getInsertedModules().forEach(type -> {
								if (moduleInv.isModuleEnabled(type))
									moduleInv.onModuleInserted(moduleInv.getModule(type), type, true);
								else
									moduleInv.onModuleRemoved(moduleInv.getModule(type), type, true);
							});
						}
					}
				}

				level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), pushedState, level.getBlockState(worldPosition), 3, 512);
				level.neighborChanged(worldPosition, pushedState.getBlock(), ExperimentalRedstoneUtils.initialOrientation(level, getPushDirection(), null));
			}
		}
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		finalTick();
	}

	public Direction getPushDirection() {
		return extending ? direction : direction.getOpposite();
	}

	public static void tick(Level level, BlockPos pos, BlockState state, ReinforcedPistonMovingBlockEntity be) {
		be.lastTicked = level.getGameTime();
		be.lastProgress = be.progress;

		if (be.lastProgress >= 1.0F) {
			if (level.isClientSide && be.deathTicks < 5)
				++be.deathTicks;
			else {
				level.removeBlockEntity(pos);
				be.setRemoved();

				if (be.movedState != null && level.getBlockState(pos).is(SCContent.REINFORCED_MOVING_PISTON.get())) {
					BlockState pushedState = Block.updateFromNeighbourShapes(be.movedState, level, pos);

					if (pushedState.isAir()) {
						level.setBlock(pos, be.movedState, 340);
						Block.updateOrDestroy(be.movedState, pushedState, level, pos, 3);
					}
					else {
						if (pushedState.hasProperty(BlockStateProperties.WATERLOGGED) && pushedState.getValue(BlockStateProperties.WATERLOGGED))
							pushedState = pushedState.setValue(BlockStateProperties.WATERLOGGED, false);

						level.setBlock(pos, pushedState, 67);

						if (be.movedBlockEntityTag != null) { //Through Level#setBlock, any previously existing BE at the given position is cleared, so the actual BE with the necessary data needs to be set afterwards
							BlockEntity storedBe = pushedState.hasBlockEntity() ? ((EntityBlock) pushedState.getBlock()).newBlockEntity(be.worldPosition, pushedState) : null;

							if (storedBe != null) {
								storedBe.loadWithComponents(be.movedBlockEntityTag);
								level.setBlockEntity(storedBe);

								if (storedBe instanceof IModuleInventory moduleInv) {
									moduleInv.getInsertedModules().forEach(type -> {
										if (moduleInv.isModuleEnabled(type))
											moduleInv.onModuleInserted(moduleInv.getModule(type), type, true);
										else
											moduleInv.onModuleRemoved(moduleInv.getModule(type), type, true);
									});
								}
							}
						}

						level.markAndNotifyBlock(pos, level.getChunkAt(pos), pushedState, level.getBlockState(pos), 67, 512);
						level.neighborChanged(pos, pushedState.getBlock(), ExperimentalRedstoneUtils.initialOrientation(level, be.getPushDirection(), null));
					}
				}
			}
		}
		else {
			float f = be.progress + 0.5F;

			moveCollidedEntities(level, pos, f, be);
			moveStuckEntities(level, pos, f, be);
			be.progress = f;

			if (be.progress >= 1.0F)
				be.progress = 1.0F;
		}
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);
		movedState = tag.read("blockState", BlockState.CODEC).orElse(DEFAULT_BLOCK_STATE);
		direction = tag.read("facing", Direction.LEGACY_ID_CODEC).orElse(Direction.DOWN);
		progress = tag.getFloatOr("progress", 0.0F);
		lastProgress = progress;
		extending = tag.getBooleanOr("extending", false);
		isSourcePiston = tag.getBooleanOr("source", false);
		movedBlockEntityTag = tag.childOrEmpty("movedBlockEntityTag");
		owner.load(tag);
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);
		tag.store("blockState", BlockState.CODEC, movedState);
		tag.store("facing", Direction.LEGACY_ID_CODEC, direction);
		tag.putFloat("progress", lastProgress);
		tag.putBoolean("extending", extending);
		tag.putBoolean("source", isSourcePiston);

		if (beToMove != null) {
			ValueOutput child = tag.child("movedBlockEntityTag");

			saveBeToMove(child);

			if (owner != null)
				owner.save(child, needsValidation());
		}
	}

	public VoxelShape getCollisionShape(BlockGetter level, BlockPos pos) {
		VoxelShape shape;

		if (!extending && isSourcePiston)
			shape = movedState.setValue(PistonBaseBlock.EXTENDED, true).getCollisionShape(level, pos);
		else
			shape = Shapes.empty();

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

			return Shapes.or(shape, state.getCollisionShape(level, pos).move(x, y, z));
		}
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
		setChanged();
	}

	public long getLastTicked() {
		return lastTicked;
	}
}
