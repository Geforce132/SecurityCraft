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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReinforcedPistonTileEntity extends TileEntity implements ITickableTileEntity, IOwnable { //this class doesn't extend PistonTileEntity because that class is almost completely private

	private BlockState pistonState;
	private CompoundNBT tileEntityTag;
	private Direction pistonFacing;
	private boolean extending;
	private boolean shouldHeadBeRendered;
	private static final ThreadLocal<Direction> MOVING_ENTITY = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;
	private long lastTicked;
	private int field_242697_l;
	private Owner owner;

	public ReinforcedPistonTileEntity() {
		super(SCContent.teTypeReinforcedPiston);
	}

	public ReinforcedPistonTileEntity(BlockState pistonStateIn, CompoundNBT tag, Direction pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn) {
		this();
		this.pistonState = pistonStateIn;
		this.tileEntityTag = tag;
		this.pistonFacing = pistonFacingIn;
		this.extending = extendingIn;
		this.shouldHeadBeRendered = shouldHeadBeRenderedIn;
		this.owner = Owner.fromCompound(tag);
	}

	/**
	 * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
	 * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
	 */
	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	/**
	 * Returns true if a piston is extending
	 */
	public boolean isExtending() {
		return this.extending;
	}

	public Direction getFacing() {
		return this.pistonFacing;
	}

	public boolean shouldPistonHeadBeRendered() {
		return this.shouldHeadBeRendered;
	}

	/**
	 * Get interpolated progress value (between lastProgress and progress) given the fractional time between ticks as an
	 * argument
	 */
	public float getProgress(float ticks) {
		if (ticks > 1.0F) {
			ticks = 1.0F;
		}

		return MathHelper.lerp(ticks, this.lastProgress, this.progress);
	}

	@OnlyIn(Dist.CLIENT)
	public float getOffsetX(float ticks) {
		return this.pistonFacing.getXOffset() * this.getExtendedProgress(this.getProgress(ticks));
	}

	@OnlyIn(Dist.CLIENT)
	public float getOffsetY(float ticks) {
		return this.pistonFacing.getYOffset() * this.getExtendedProgress(this.getProgress(ticks));
	}

	@OnlyIn(Dist.CLIENT)
	public float getOffsetZ(float ticks) {
		return this.pistonFacing.getZOffset() * this.getExtendedProgress(this.getProgress(ticks));
	}

	private float getExtendedProgress(float p_184320_1_) {
		return this.extending ? p_184320_1_ - 1.0F : 1.0F - p_184320_1_;
	}

	private BlockState getCollisionRelatedBlockState() {
		return !this.isExtending() && this.shouldPistonHeadBeRendered() && this.pistonState.getBlock() instanceof ReinforcedPistonBlock ? SCContent.REINFORCED_PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.TYPE, this.pistonState.getBlock() == SCContent.REINFORCED_STICKY_PISTON.get() ? PistonType.STICKY : PistonType.DEFAULT).with(PistonHeadBlock.FACING, this.pistonState.get(PistonBlock.FACING)) : this.pistonState;
	}

	private void moveCollidedEntities(float p_184322_1_) {
		Direction direction = this.getMotionDirection();
		double d0 = (double)(progress - this.progress);
		VoxelShape voxelshape = this.getCollisionRelatedBlockState().getCollisionShapeUncached(this.world, this.getPos());
		if (!voxelshape.isEmpty()) {
			AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(voxelshape.getBoundingBox());
			List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, AabbHelper.func_227019_a_(axisalignedbb, direction, d0).union(axisalignedbb));
			if (!list.isEmpty()) {
				List<AxisAlignedBB> list1 = voxelshape.toBoundingBoxList();
				boolean flag = this.pistonState.isSlimeBlock(); //TODO: is this patch really needed the logic of the original seems sound revisit later
				Iterator iterator = list.iterator();

				while(true) {
					Entity entity;
					while(true) {
						if (!iterator.hasNext()) {
							return;
						}

						entity = (Entity)iterator.next();
						if (entity.getPushReaction() != PushReaction.IGNORE) {
							if (!flag) {
								break;
							}

							if (!(entity instanceof ServerPlayerEntity)) {
								Vector3d vector3d = entity.getMotion();
								double d1 = vector3d.x;
								double d2 = vector3d.y;
								double d3 = vector3d.z;
								switch(direction.getAxis()) {
									case X:
										d1 = direction.getXOffset();
										break;
									case Y:
										d2 = direction.getYOffset();
										break;
									case Z:
										d3 = direction.getZOffset();
								}

								entity.setMotion(d1, d2, d3);
								break;
							}
						}
					}

					double d4 = 0.0D;

					for(AxisAlignedBB axisalignedbb2 : list1) {
						AxisAlignedBB axisalignedbb1 = AabbHelper.func_227019_a_(this.moveByPositionAndProgress(axisalignedbb2), direction, d0);
						AxisAlignedBB axisalignedbb3 = entity.getBoundingBox();
						if (axisalignedbb1.intersects(axisalignedbb3)) {
							d4 = Math.max(d4, getMovement(axisalignedbb1, direction, axisalignedbb3));
							if (d4 >= d0) {
								break;
							}
						}
					}

					if (!(d4 <= 0.0D)) {
						d4 = Math.min(d4, d0) + 0.01D;
						pushEntity(direction, entity, d4, direction);
						if (!this.extending && this.shouldHeadBeRendered) {
							this.fixEntityWithinPistonBase(entity, direction, d0);
						}
					}
				}
			}
		}
	}

	private static void pushEntity(Direction direction, Entity entity, double progress, Direction p_227022_4_) {
		MOVING_ENTITY.set(direction);
		entity.move(MoverType.PISTON, new Vector3d(progress * (double)p_227022_4_.getXOffset(), progress * (double)p_227022_4_.getYOffset(), progress * (double)p_227022_4_.getZOffset()));
		MOVING_ENTITY.set(null);
	}

	private void func_227024_g_(float progress) {
		if (this.isHoney()) {
			Direction direction = this.getMotionDirection();
			if (direction.getAxis().isHorizontal()) {
				double d0 = this.pistonState.getCollisionShapeUncached(this.world, this.pos).getEnd(Direction.Axis.Y);
				AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(new AxisAlignedBB(0.0D, d0, 0.0D, 1.0D, 1.5000000999999998D, 1.0D));
				double d1 = (progress - this.progress);

				for(Entity entity : this.world.getEntitiesInAABBexcluding(null, axisalignedbb, (entity) -> canPushEntity(axisalignedbb, entity))) {
					pushEntity(direction, entity, d1, direction);
				}

			}
		}
	}

	private static boolean canPushEntity(AxisAlignedBB shape, Entity entity) {
		return entity.getPushReaction() == PushReaction.NORMAL && entity.isOnGround() && entity.getPosX() >= shape.minX && entity.getPosX() <= shape.maxX && entity.getPosZ() >= shape.minZ && entity.getPosZ() <= shape.maxZ;
	}

	private boolean isHoney() {
		return this.pistonState.matchesBlock(Blocks.HONEY_BLOCK);
	}

	public Direction getMotionDirection() {
		return this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
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
		double d0 = this.getExtendedProgress(this.progress);
		return boundingBox.offset((double)this.pos.getX() + d0 * (double)this.pistonFacing.getXOffset(), (double)this.pos.getY() + d0 * (double)this.pistonFacing.getYOffset(), (double)this.pos.getZ() + d0 * (double)this.pistonFacing.getZOffset());
	}

	private void fixEntityWithinPistonBase(Entity p_190605_1_, Direction p_190605_2_, double p_190605_3_) {
		AxisAlignedBB axisalignedbb = p_190605_1_.getBoundingBox();
		AxisAlignedBB axisalignedbb1 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
		if (axisalignedbb.intersects(axisalignedbb1)) {
			Direction direction = p_190605_2_.getOpposite();
			double d0 = getMovement(axisalignedbb1, direction, axisalignedbb) + 0.01D;
			double d1 = getMovement(axisalignedbb1, direction, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;
			if (Math.abs(d0 - d1) < 0.01D) {
				d0 = Math.min(d0, progress) + 0.01D;
				pushEntity(p_190605_2_, p_190605_1_, d0, direction);
			}
		}
	}

	public BlockState getPistonState() {
		return this.pistonState;
	}

	/**
	 * removes a piston's tile entity (and if the piston is moving, stops it)
	 */
	public void clearPistonTileEntity() {
		if (this.world != null && (this.lastProgress < 1.0F || this.world.isRemote)) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeTileEntity(this.pos);
			this.remove();
			if (this.world.getBlockState(this.pos).matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get())) {
				BlockState pushedState;
				if (this.shouldHeadBeRendered) {
					pushedState = Blocks.AIR.getDefaultState();
				} else {
					pushedState = Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
				}

				this.world.setBlockState(this.pos, pushedState, 3);

				if (tileEntityTag != null){
					TileEntity te = pushedState.hasTileEntity() ? pushedState.createTileEntity(this.world) : null;
					if (te != null){
						te.read(this.pistonState, tileEntityTag);
						this.world.setTileEntity(this.pos, te);
						this.world.markChunkDirty(this.pos, te);
					}
				}

				this.world.neighborChanged(this.pos, pushedState.getBlock(), this.pos);
			}
		}
	}

	@Override
	public void tick() {
		this.lastTicked = this.world.getGameTime();
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			if (this.world.isRemote && this.field_242697_l < 5) {
				++this.field_242697_l;
			} else {
				this.world.removeTileEntity(this.pos);
				this.remove();
				if (this.pistonState != null && this.world.getBlockState(this.pos).matchesBlock(SCContent.REINFORCED_MOVING_PISTON.get())) {
					BlockState pushedState = Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
					if (pushedState.isAir()) {
						this.world.setBlockState(this.pos, this.pistonState, 84);
						Block.replaceBlock(this.pistonState, pushedState, this.world, this.pos, 3);
					} else {
						if (pushedState.hasProperty(BlockStateProperties.WATERLOGGED) && pushedState.get(BlockStateProperties.WATERLOGGED)) {
							pushedState = pushedState.with(BlockStateProperties.WATERLOGGED, false);
						}

						this.world.setBlockState(this.pos, pushedState, 67);

						//System.out.println("blockstate: " + pushedState);
						if (tileEntityTag != null){
							//System.out.println("pre-saving te: " + tagIn);
							TileEntity te = pushedState.hasTileEntity() ? pushedState.createTileEntity(this.world) : null;
							if (te != null){
								//System.out.println("te not null:");
								te.read(this.pistonState, tileEntityTag);
								this.world.setTileEntity(this.pos, te);
								this.world.markChunkDirty(this.pos, te);
							}
						}

						this.world.neighborChanged(this.pos, pushedState.getBlock(), this.pos);
					}
				}
			}
		} else {
			float f = this.progress + 0.5F;
			this.moveCollidedEntities(f);
			this.func_227024_g_(f);
			this.progress = f;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);
		this.pistonState = NBTUtil.readBlockState(compound.getCompound("blockState"));
		this.pistonFacing = Direction.byIndex(compound.getInt("facing"));
		this.progress = compound.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = compound.getBoolean("extending");
		this.shouldHeadBeRendered = compound.getBoolean("source");
		this.tileEntityTag = compound.getCompound("originalTag");

		if (compound.contains("owner"))
			owner.setOwnerName(compound.getString("owner"));

		if (compound.contains("ownerUUID"))
			owner.setOwnerUUID(compound.getString("ownerUUID"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("blockState", NBTUtil.writeBlockState(this.pistonState));
		compound.putInt("facing", this.pistonFacing.getIndex());
		compound.putFloat("progress", this.lastProgress);
		compound.putBoolean("extending", this.extending);
		compound.putBoolean("source", this.shouldHeadBeRendered);
		compound.put("originalTag", this.tileEntityTag);

		if(owner != null){
			compound.putString("owner", owner.getName());
			compound.putString("ownerUUID", owner.getUUID());
		}

		return compound;
	}

	public VoxelShape getCollisionShape(IBlockReader p_195508_1_, BlockPos p_195508_2_) {
		VoxelShape voxelshape;
		if (!this.extending && this.shouldHeadBeRendered) {
			voxelshape = this.pistonState.with(PistonBlock.EXTENDED, true).getCollisionShape(p_195508_1_, p_195508_2_);
		} else {
			voxelshape = VoxelShapes.empty();
		}

		Direction direction = MOVING_ENTITY.get();
		if (this.progress < 1.0D && direction == this.getMotionDirection()) {
			return voxelshape;
		} else {
			BlockState blockstate;
			if (this.shouldPistonHeadBeRendered()) {
				blockstate = SCContent.REINFORCED_PISTON_HEAD.get().getDefaultState().with(PistonHeadBlock.FACING, this.pistonFacing).with(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 4.0F);
			} else {
				blockstate = this.pistonState;
			}

			float f = this.getExtendedProgress(this.progress);
			double d0 = this.pistonFacing.getXOffset() * f;
			double d1 = this.pistonFacing.getYOffset() * f;
			double d2 = this.pistonFacing.getZOffset() * f;
			return VoxelShapes.or(voxelshape, blockstate.getCollisionShape(p_195508_1_, p_195508_2_).withOffset(d0, d1, d2));
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
		return this.lastTicked;
	}

	public double getMaxRenderDistanceSquared() {
		return 68.0D;
	}
}
