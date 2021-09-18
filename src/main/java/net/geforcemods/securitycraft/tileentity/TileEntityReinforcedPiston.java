package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TileEntityReinforcedPiston extends TileEntity implements IOwnable, ITickable { //this class doesn't extend TileEntityPiston because almost all of that class' content is private

	private IBlockState pistonState;
	private NBTTagCompound tileEntityTag;
	private EnumFacing pistonFacing;
	private boolean extending;
	private boolean shouldHeadBeRendered;
	private static final ThreadLocal<EnumFacing> MOVING_ENTITY = ThreadLocal.withInitial(() -> null);
	public float progress;
	public float lastProgress;
	private Owner owner = new Owner();

	public TileEntityReinforcedPiston() {
	}

	public TileEntityReinforcedPiston(IBlockState pistonState, NBTTagCompound tag, EnumFacing pistonFacing, boolean extending, boolean shouldHeadBeRendered) {
		this.pistonState = pistonState;
		this.tileEntityTag = tag;
		this.pistonFacing = pistonFacing;
		this.extending = extending;
		this.shouldHeadBeRendered = shouldHeadBeRendered;
		this.owner = Owner.fromCompound(tag);
	}

	public IBlockState getPistonState() {
		return pistonState;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public int getBlockMetadata() {
		return 0;
	}

	public boolean isExtending() {
		return extending;
	}

	public EnumFacing getFacing() {
		return pistonFacing;
	}

	public boolean shouldPistonHeadBeRendered() {
		return shouldHeadBeRendered;
	}

	/**
	 * Get interpolated progress value (between lastProgress and progress) given the fractional time between ticks as an
	 * argument
	 */
	public float getProgress(float ticks) {
		if (ticks > 1.0F) {
			ticks = 1.0F;
		}

		return lastProgress + (progress - lastProgress) * ticks;
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

	public AxisAlignedBB getAABB(IBlockAccess world, BlockPos pos) {
		return getAABB(world, pos, progress).union(getAABB(world, pos, lastProgress));
	}

	public AxisAlignedBB getAABB(IBlockAccess world, BlockPos pos, float progress) {
		IBlockState state = getCollisionRelatedBlockState();

		progress = getExtendedProgress(progress);
		return state.getBoundingBox(world, pos).offset(progress * pistonFacing.getXOffset(), progress * pistonFacing.getYOffset(), progress * pistonFacing.getZOffset());
	}

	private IBlockState getCollisionRelatedBlockState() {
		return !isExtending() && shouldPistonHeadBeRendered() ? SCContent.reinforcedPistonHead.getDefaultState().withProperty(BlockPistonExtension.TYPE, pistonState.getBlock() == SCContent.reinforcedStickyPiston ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT).withProperty(BlockPistonExtension.FACING, pistonState.getValue(BlockPistonBase.FACING)) : pistonState;
	}

	private void moveCollidedEntities(float progress) {
		EnumFacing facing = extending ? pistonFacing : pistonFacing.getOpposite();
		double d0 = progress - this.progress;
		List<AxisAlignedBB> collisionShape = Lists.newArrayList();

		getCollisionRelatedBlockState().addCollisionBoxToList(world, BlockPos.ORIGIN, new AxisAlignedBB(BlockPos.ORIGIN), collisionShape, null, true);

		if (!collisionShape.isEmpty()) {
			AxisAlignedBB boundingBox = moveByPositionAndProgress(getMinMaxPiecesAABB(collisionShape));
			List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, getMovementArea(boundingBox, facing, d0).union(boundingBox));

			if (!entities.isEmpty()) {
				boolean isStickyBlock = pistonState.getBlock().isStickyBlock(pistonState);

				for (int i = 0; i < entities.size(); ++i) {
					Entity entity = entities.get(i);

					if (entity.getPushReaction() != EnumPushReaction.IGNORE) {
						if (isStickyBlock) {
							switch (facing.getAxis()) {
								case X:
									entity.motionX = facing.getXOffset();
									break;
								case Y:
									entity.motionY = facing.getYOffset();
									break;
								case Z:
									entity.motionZ = facing.getZOffset();
							}
						}

						double movement = 0.0D;

						for (int j = 0; j < collisionShape.size(); ++j) {
							AxisAlignedBB movementArea = getMovementArea(moveByPositionAndProgress(collisionShape.get(j)), facing, d0);
							AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();

							if (movementArea.intersects(entityBoundingBox)) {
								movement = Math.max(movement, getMovement(movementArea, facing, entityBoundingBox));

								if (movement >= d0) {
									break;
								}
							}
						}

						if (movement > 0.0D) {
							movement = Math.min(movement, d0) + 0.01D;
							MOVING_ENTITY.set(facing);
							entity.move(MoverType.PISTON, movement * facing.getXOffset(), movement * facing.getYOffset(), movement * facing.getZOffset());
							MOVING_ENTITY.set(null);

							if (!extending && shouldHeadBeRendered) {
								fixEntityWithinPistonBase(entity, facing, d0);
							}
						}
					}
				}
			}
		}
	}

	private AxisAlignedBB getMinMaxPiecesAABB(List<AxisAlignedBB> boundingBoxes) {
		double minX = 0.0D;
		double minY = 0.0D;
		double minZ = 0.0D;
		double maxX = 1.0D;
		double maxY = 1.0D;
		double maxZ = 1.0D;

		for (AxisAlignedBB axisalignedbb : boundingBoxes) {
			minX = Math.min(axisalignedbb.minX, minX);
			minY = Math.min(axisalignedbb.minY, minY);
			minZ = Math.min(axisalignedbb.minZ, minZ);
			maxX = Math.max(axisalignedbb.maxX, maxX);
			maxY = Math.max(axisalignedbb.maxY, maxY);
			maxZ = Math.max(axisalignedbb.maxZ, maxZ);
		}

		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	private double getMovement(AxisAlignedBB boundingBox, EnumFacing facing, AxisAlignedBB entityBoundingBox) {
		switch (facing.getAxis()) {
			case X:
				return getDeltaX(boundingBox, facing, entityBoundingBox);
			case Y:
			default:
				return getDeltaY(boundingBox, facing, entityBoundingBox);
			case Z:
				return getDeltaZ(boundingBox, facing, entityBoundingBox);
		}
	}

	private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB boundingBox)
	{
		double progress = getExtendedProgress(this.progress);
		return boundingBox.offset(pos.getX() + progress * pistonFacing.getXOffset(), pos.getY() + progress * pistonFacing.getYOffset(), pos.getZ() + progress * pistonFacing.getZOffset());
	}

	private AxisAlignedBB getMovementArea(AxisAlignedBB boundingBox, EnumFacing facing, double progress) {
		double d0 = progress * facing.getAxisDirection().getOffset();
		double d1 = Math.min(d0, 0.0D);
		double d2 = Math.max(d0, 0.0D);

		switch (facing) {
			case WEST:
				return new AxisAlignedBB(boundingBox.minX + d1, boundingBox.minY, boundingBox.minZ, boundingBox.minX + d2, boundingBox.maxY, boundingBox.maxZ);
			case EAST:
				return new AxisAlignedBB(boundingBox.maxX + d1, boundingBox.minY, boundingBox.minZ, boundingBox.maxX + d2, boundingBox.maxY, boundingBox.maxZ);
			case DOWN:
				return new AxisAlignedBB(boundingBox.minX, boundingBox.minY + d1, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + d2, boundingBox.maxZ);
			case UP:
			default:
				return new AxisAlignedBB(boundingBox.minX, boundingBox.maxY + d1, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY + d2, boundingBox.maxZ);
			case NORTH:
				return new AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.minZ + d1, boundingBox.maxX, boundingBox.maxY, boundingBox.minZ + d2);
			case SOUTH:
				return new AxisAlignedBB(boundingBox.minX, boundingBox.minY, boundingBox.maxZ + d1, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ + d2);
		}
	}

	private void fixEntityWithinPistonBase(Entity entity, EnumFacing pushDirection, double progress) {
		AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
		AxisAlignedBB pistonBoundingBox = Block.FULL_BLOCK_AABB.offset(pos);

		if (entityBoundingBox.intersects(pistonBoundingBox)) {
			EnumFacing facing = pushDirection.getOpposite();
			double d0 = getMovement(pistonBoundingBox, facing, entityBoundingBox) + 0.01D;
			double d1 = getMovement(pistonBoundingBox, facing, entityBoundingBox.intersect(pistonBoundingBox)) + 0.01D;

			if (Math.abs(d0 - d1) < 0.01D) {
				d0 = Math.min(d0, progress) + 0.01D;
				MOVING_ENTITY.set(pushDirection);
				entity.move(MoverType.PISTON, d0 * facing.getXOffset(), d0 * facing.getYOffset(), d0 * facing.getZOffset());
				MOVING_ENTITY.set(null);
			}
		}
	}

	private static double getDeltaX(AxisAlignedBB boundingBox, EnumFacing facing, AxisAlignedBB entityBoundingBox) {
		return facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? boundingBox.maxX - entityBoundingBox.minX : entityBoundingBox.maxX - boundingBox.minX;
	}

	private static double getDeltaY(AxisAlignedBB boundingBox, EnumFacing facing, AxisAlignedBB entityBoundingBox) {
		return facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? boundingBox.maxY - entityBoundingBox.minY : entityBoundingBox.maxY - boundingBox.minY;
	}

	private static double getDeltaZ(AxisAlignedBB boundingBox, EnumFacing facing, AxisAlignedBB entityBoundingBox) {
		return facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? boundingBox.maxZ - entityBoundingBox.minZ : entityBoundingBox.maxZ - boundingBox.minZ;
	}

	public void clearPistonTileEntity() {
		if (lastProgress < 1.0F && world != null) {
			progress = 1.0F;
			lastProgress = progress;
			world.removeTileEntity(pos);
			invalidate();

			if (world.getBlockState(pos).getBlock() == SCContent.reinforcedPistonExtension) {
				IBlockState pushedState = getPistonState();
				Block pushedBlock = pushedState.getBlock();

				if (tileEntityTag != null){
					TileEntity te = pushedBlock.hasTileEntity(pushedState) ? pushedBlock.createTileEntity(world, pushedState) : null;

					if (te != null){
						Chunk chunk = world.getChunk(pos);

						te.readFromNBT(tileEntityTag);
						chunk.addTileEntity(pos, te);
						world.setTileEntity(pos, te);
					}
				}

				world.setBlockState(pos, pushedState, 3);
				world.neighborChanged(pos, pushedState.getBlock(), pos);
			}
		}
	}

	@Override
	public void update() {
		this.lastProgress = progress;

		if (lastProgress >= 1.0F) {
			world.removeTileEntity(pos);
			invalidate();

			if (world.getBlockState(pos).getBlock() == SCContent.reinforcedPistonExtension) {
				IBlockState pushedState = getPistonState();
				Block pushedBlock = pushedState.getBlock();

				if (tileEntityTag != null) {
					TileEntity te = pushedBlock.hasTileEntity(pushedState) ? pushedBlock.createTileEntity(world, pushedState) : null;

					if (te != null) {
						Chunk chunk = world.getChunk(pos);

						te.readFromNBT(tileEntityTag);
						chunk.addTileEntity(pos, te);
						world.setTileEntity(pos, te);
					}
				}

				world.setBlockState(pos, pushedState, 3);
				world.neighborChanged(pos, pushedBlock, pos);
			}
		}
		else {
			float f = progress + 0.5F;
			moveCollidedEntities(f);
			progress = f;

			if (progress >= 1.0F) {
				progress = 1.0F;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		pistonState = Block.getBlockById(compound.getInteger("blockId")).getStateFromMeta(compound.getInteger("blockData"));
		pistonFacing = EnumFacing.byIndex(compound.getInteger("facing"));
		progress = compound.getFloat("progress");
		lastProgress = progress;
		extending = compound.getBoolean("extending");
		shouldHeadBeRendered = compound.getBoolean("source");
		tileEntityTag = compound.getCompoundTag("movedTileEntityTag");
		owner.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("blockId", Block.getIdFromBlock(pistonState.getBlock()));
		compound.setInteger("blockData", pistonState.getBlock().getMetaFromState(pistonState));
		compound.setInteger("facing", pistonFacing.getIndex());
		compound.setFloat("progress", lastProgress);
		compound.setBoolean("extending", extending);
		compound.setBoolean("source", shouldHeadBeRendered);
		compound.setTag("movedTileEntityTag", tileEntityTag);

		if(owner != null){
			owner.writeToNBT(compound, false);
		}

		return compound;
	}

	public void addCollisionAABBs(World world, BlockPos pos, AxisAlignedBB entityBoundingBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity) {
		if (!extending && shouldHeadBeRendered) {
			pistonState.withProperty(BlockPistonBase.EXTENDED, true).addCollisionBoxToList(world, pos, entityBoundingBox, collidingBoxes, entity, false);
		}

		EnumFacing enumfacing = MOVING_ENTITY.get();

		if (progress >= 1.0D || enumfacing != (extending ? pistonFacing : pistonFacing.getOpposite())) {
			int i = collidingBoxes.size();
			IBlockState state;

			if (shouldPistonHeadBeRendered()) {
				state = SCContent.reinforcedPistonHead.getDefaultState().withProperty(BlockPistonExtension.FACING, pistonFacing).withProperty(BlockPistonExtension.SHORT, extending != 1.0F - progress < 0.25F);
			}
			else {
				state = pistonState;
			}

			float progress = getExtendedProgress(this.progress);
			double d0 = (float)pistonFacing.getXOffset() * progress;
			double d1 = (float)pistonFacing.getYOffset() * progress;
			double d2 = (float)pistonFacing.getZOffset() * progress;

			state.addCollisionBoxToList(world, pos, entityBoundingBox.offset(-d0, -d1, -d2), collidingBoxes, entity, true);

			for (int j = i; j < collidingBoxes.size(); ++j) {
				collidingBoxes.set(j, collidingBoxes.get(j).offset(d0, d1, d2));
			}
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
}
