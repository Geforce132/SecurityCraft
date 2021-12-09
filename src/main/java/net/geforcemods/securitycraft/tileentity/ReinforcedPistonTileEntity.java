package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class ReinforcedPistonTileEntity extends TileEntity implements ITickableTileEntity, IOwnable { //this class doesn't extend PistonTileEntity because almost all of that class' content is private

	private BlockState pistonState;
	private CompoundNBT tileEntityTag;
	private Direction pistonFacing;
	private boolean extending;
	private boolean shouldHeadBeRendered;
	private static final ThreadLocal<Direction> MOVING_ENTITY = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;
	private long lastTicked;
	private int deathTicks;
	private Owner owner = new Owner();

	public ReinforcedPistonTileEntity() {
		super(SCContent.teTypeReinforcedPiston);
	}

	public ReinforcedPistonTileEntity(BlockState pistonState, CompoundNBT tag, Direction pistonFacing, boolean extending, boolean shouldHeadBeRendered) {
		super(SCContent.teTypeReinforcedPiston);
		this.pistonState = pistonState;
		this.tileEntityTag = tag;
		this.pistonFacing = pistonFacing;
		this.extending = extending;
		this.shouldHeadBeRendered = shouldHeadBeRendered;
		this.owner = Owner.fromCompound(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	public boolean isExtending() {
		return extending;
	}

	public Direction getFacing() {
		return pistonFacing;
	}

	public boolean shouldPistonHeadBeRendered() {
		return shouldHeadBeRendered;
	}

	public float getProgress(float ticks) {
		if (ticks > 1.0F) {
			ticks = 1.0F;
		}

		return MathHelper.lerp(ticks, lastProgress, progress);
	}

	public float getOffsetX(float ticks) {
		return pistonFacing.getXOffset() * getExtendedProgress(getProgress(ticks));
	}

	public float getOffsetY(float ticks) {
		return pistonFacing.getYOffset() * getExtendedProgress(getProgress(ticks));
	}

	public float getOffsetZ(float ticks) {
		return pistonFacing.getZOffset() * getExtendedProgress(getProgress(ticks));
	}

	private float getExtendedProgress(float progress) {
		return extending ? progress - 1.0F : 1.0F - progress;
	}

	private BlockState getCollisionRelatedBlockState() {
		return !isExtending() && shouldPistonHeadBeRendered() && pistonState.getBlock() instanceof ReinforcedPistonBlock ? SCContent.REINFORCED_PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.TYPE, pistonState.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get() ? PistonType.STICKY : PistonType.DEFAULT).with(PistonHeadBlock.FACING, pistonState.get(PistonBlock.FACING)) : pistonState;
	}

	private void moveCollidedEntities(float progress) {
		Direction direction = getMotionDirection();
		double d0 = progress - this.progress;
		VoxelShape collisionShape = getCollisionRelatedBlockState().getCollisionShapeUncached(world, getPos());

		if (!collisionShape.isEmpty()) {
			AxisAlignedBB boundingBox = moveByPositionAndProgress(collisionShape.getBoundingBox());
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity((Entity)null, AabbHelper.func_227019_a_(boundingBox, direction, d0).union(boundingBox));

			if (!list.isEmpty()) {
				List<AxisAlignedBB> boundingBoxes = collisionShape.toBoundingBoxList();
				boolean isSlimeBlock = pistonState.isSlimeBlock();
				Iterator<Entity> iterator = list.iterator();

				while(true) {
					Entity entity;

					while(true) {
						if (!iterator.hasNext()) {
							return;
						}

						entity = iterator.next();
						if (entity.getPushReaction() != PushReaction.IGNORE) {
							if (!isSlimeBlock) {
								break;
							}

							if (!(entity instanceof ServerPlayerEntity)) {
								Vector3d vector3d = entity.getMotion();
								double x = vector3d.x;
								double y = vector3d.y;
								double z = vector3d.z;

								switch(direction.getAxis()) {
									case X:
										x = direction.getXOffset();
										break;
									case Y:
										y = direction.getYOffset();
										break;
									case Z:
										z = direction.getZOffset();
								}

								entity.setMotion(x, y, z);
								break;
							}
						}
					}

					double movement = 0.0D;

					for(AxisAlignedBB aabb : boundingBoxes) {
						AxisAlignedBB movementArea = AabbHelper.func_227019_a_(moveByPositionAndProgress(aabb), direction, d0);
						AxisAlignedBB entityBoundingBox = entity.getBoundingBox();

						if (movementArea.intersects(entityBoundingBox)) {
							movement = Math.max(movement, getMovement(movementArea, direction, entityBoundingBox));
							if (movement >= d0) {
								break;
							}
						}
					}

					if (!(movement <= 0.0D)) {
						movement = Math.min(movement, d0) + 0.01D;
						pushEntity(direction, entity, movement, direction);

						if (!extending && shouldHeadBeRendered) {
							fixEntityWithinPistonBase(entity, direction, d0);
						}
					}
				}
			}
		}
	}

	private static void pushEntity(Direction direction, Entity entity, double progress, Direction moveDirection) {
		MOVING_ENTITY.set(direction);
		entity.move(MoverType.PISTON, new Vector3d(progress * moveDirection.getXOffset(), progress * moveDirection.getYOffset(), progress * moveDirection.getZOffset()));
		MOVING_ENTITY.set(null);
	}

	private void moveStuckEntities(float progress) {
		if (isHoney()) {
			Direction direction = getMotionDirection();

			if (direction.getAxis().isHorizontal()) {
				double collisionShapeTop = pistonState.getCollisionShapeUncached(world, pos).getEnd(Direction.Axis.Y);
				AxisAlignedBB axisalignedbb = moveByPositionAndProgress(new AxisAlignedBB(0.0D, collisionShapeTop, 0.0D, 1.0D, 1.5000000999999998D, 1.0D));
				double d1 = progress - this.progress;

				for(Entity entity : world.getEntitiesInAABBexcluding(null, axisalignedbb, entity -> canPushEntity(axisalignedbb, entity))) {
					pushEntity(direction, entity, d1, direction);
				}

			}
		}
	}

	private static boolean canPushEntity(AxisAlignedBB shape, Entity entity) {
		return entity.getPushReaction() == PushReaction.NORMAL && entity.isOnGround() && entity.getPosX() >= shape.minX && entity.getPosX() <= shape.maxX && entity.getPosZ() >= shape.minZ && entity.getPosZ() <= shape.maxZ;
	}

	private boolean isHoney() {
		return pistonState.matchesBlock(Blocks.HONEY_BLOCK);
	}

	public Direction getMotionDirection() {
		return extending ? pistonFacing : pistonFacing.getOpposite();
	}

	private static double getMovement(AxisAlignedBB headShape, Direction direction, AxisAlignedBB facing) {
		switch(direction) {
			case EAST:
				return headShape.maxX - facing.minX;
			case WEST:
				return facing.maxX - headShape.minX;
			case UP:
			default:
				return headShape.maxY - facing.minY;
			case DOWN:
				return facing.maxY - headShape.minY;
			case SOUTH:
				return headShape.maxZ - facing.minZ;
			case NORTH:
				return facing.maxZ - headShape.minZ;
		}
	}

	private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB boundingBox) {
		double extendedProgress = getExtendedProgress(progress);
		return boundingBox.offset(pos.getX() + extendedProgress * pistonFacing.getXOffset(), pos.getY() + extendedProgress * pistonFacing.getYOffset(), pos.getZ() + extendedProgress * pistonFacing.getZOffset());
	}

	private void fixEntityWithinPistonBase(Entity entity, Direction pushDirection, double progress) {
		AxisAlignedBB entityBoundingBox = entity.getBoundingBox();
		AxisAlignedBB pistonBoundingBox = VoxelShapes.fullCube().getBoundingBox().offset(pos);

		if (entityBoundingBox.intersects(pistonBoundingBox)) {
			Direction direction = pushDirection.getOpposite();
			double d0 = getMovement(pistonBoundingBox, direction, entityBoundingBox) + 0.01D;
			double d1 = getMovement(pistonBoundingBox, direction, entityBoundingBox.intersect(pistonBoundingBox)) + 0.01D;

			if (Math.abs(d0 - d1) < 0.01D) {
				d0 = Math.min(d0, progress) + 0.01D;
				pushEntity(pushDirection, entity, d0, direction);
			}
		}
	}

	public BlockState getPistonState() {
		return pistonState;
	}

	/**
	 * removes a piston's tile entity (and if the piston is moving, stops it)
	 */
	public void clearPistonTileEntity() {
		if (world != null && (lastProgress < 1.0F || world.isRemote)) {
			progress = 1.0F;
			lastProgress = progress;
			world.removeTileEntity(pos);
			remove();

			if (world.getBlockState(pos).matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get())) {
				BlockState pushedState;

				if (shouldHeadBeRendered) {
					pushedState = Blocks.AIR.getDefaultState();
				} else {
					pushedState = Block.getValidBlockForPosition(pistonState, world, pos);
				}

				if (tileEntityTag != null){
					TileEntity te = pushedState.hasTileEntity() ? pushedState.createTileEntity(world) : null;

					if (te != null){
						Chunk chunk = world.getChunkAt(pos);

						te.read(pistonState, tileEntityTag);
						chunk.addTileEntity(pos, te);
					}
				}

				world.setBlockState(pos, pushedState, 3);
				world.neighborChanged(pos, pushedState.getBlock(), pos);
			}
		}
	}

	@Override
	public void tick() {
		lastTicked = world.getGameTime();
		lastProgress = progress;

		if (lastProgress >= 1.0F) {
			if (world.isRemote && deathTicks < 5) {
				++deathTicks;
			} else {
				world.removeTileEntity(pos);
				remove();

				if (pistonState != null && world.getBlockState(pos).matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get())) {
					BlockState pushedState = Block.getValidBlockForPosition(pistonState, world, pos);

					if (pushedState.isAir()) {
						world.setBlockState(pos, pistonState, 84);
						Block.replaceBlock(pistonState, pushedState, world, pos, 3);
					} else {
						if (pushedState.hasProperty(BlockStateProperties.WATERLOGGED) && pushedState.get(BlockStateProperties.WATERLOGGED)) {
							pushedState = pushedState.with(BlockStateProperties.WATERLOGGED, false);
						}

						if (tileEntityTag != null){
							TileEntity te = pushedState.hasTileEntity() ? pushedState.createTileEntity(world) : null;

							if (te != null){
								Chunk chunk = world.getChunkAt(pos);

								te.read(pistonState, tileEntityTag);
								chunk.addTileEntity(pos, te);
							}
						}

						world.setBlockState(pos, pushedState, 67);
						world.neighborChanged(pos, pushedState.getBlock(), pos);
					}
				}
			}
		} else {
			float f = progress + 0.5F;

			moveCollidedEntities(f);
			moveStuckEntities(f);
			progress = f;

			if (progress >= 1.0F) {
				progress = 1.0F;
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);
		pistonState = NBTUtil.readBlockState(compound.getCompound("blockState"));
		pistonFacing = Direction.byIndex(compound.getInt("facing"));
		progress = compound.getFloat("progress");
		lastProgress = progress;
		extending = compound.getBoolean("extending");
		shouldHeadBeRendered = compound.getBoolean("source");
		tileEntityTag = compound.getCompound("movedTileEntityTag");
		owner.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("blockState", NBTUtil.writeBlockState(pistonState));
		compound.putInt("facing", pistonFacing.getIndex());
		compound.putFloat("progress", lastProgress);
		compound.putBoolean("extending", extending);
		compound.putBoolean("source", shouldHeadBeRendered);
		compound.put("movedTileEntityTag", tileEntityTag);

		if(owner != null){
			owner.write(compound, false);
		}

		return compound;
	}

	public VoxelShape getCollisionShape(IBlockReader world, BlockPos pos) {
		VoxelShape shape;

		if (!extending && shouldHeadBeRendered) {
			shape = pistonState.with(PistonBlock.EXTENDED, true).getCollisionShape(world, pos);
		} else {
			shape = VoxelShapes.empty();
		}

		if (progress < 1.0D && MOVING_ENTITY.get() == getMotionDirection()) {
			return shape;
		} else {
			BlockState state;

			if (shouldPistonHeadBeRendered()) {
				state = SCContent.REINFORCED_PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.FACING, pistonFacing).with(PistonHeadBlock.SHORT, extending != 1.0F - progress < 4.0F);
			} else {
				state = pistonState;
			}

			float f = getExtendedProgress(progress);
			double d0 = pistonFacing.getXOffset() * f;
			double d1 = pistonFacing.getYOffset() * f;
			double d2 = pistonFacing.getZOffset() * f;

			return VoxelShapes.or(shape, state.getCollisionShape(world, pos).withOffset(d0, d1, d2));
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

	@Override
	public double getMaxRenderDistanceSquared() {
		return 68.0D;
	}
}
