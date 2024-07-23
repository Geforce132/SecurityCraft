package net.geforcemods.securitycraft.entity.camera;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SecurityCamera extends Entity {
	public static final DataParameter<Float> ZOOM_AMOUNT = EntityDataManager.<Float>defineId(SecurityCamera.class, DataSerializers.FLOAT);
	private static final List<PlayerEntity> DISMOUNTED_PLAYERS = new ArrayList<>();
	protected boolean zooming = false;
	private int initialChunkLoadingDistance = 0;
	private boolean hasSentChunks = false;
	private SecurityCameraBlockEntity be;

	public SecurityCamera(EntityType<? extends SecurityCamera> type, World level) {
		super(SCContent.SECURITY_CAMERA_ENTITY.get(), level);
		noPhysics = true;
		forcedLoading = true;
	}

	public SecurityCamera(World level) {
		this(SCContent.SECURITY_CAMERA_ENTITY.get(), level);
	}

	public SecurityCamera(World level, BlockPos pos) {
		this(SCContent.SECURITY_CAMERA_ENTITY.get(), level);

		TileEntity te = level.getBlockEntity(pos);

		if (!(te instanceof SecurityCameraBlockEntity)) {
			remove();
			return;
		}

		SecurityCameraBlockEntity cam = (SecurityCameraBlockEntity) te;
		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.5D;
		double z = pos.getZ() + 0.5D;

		be = cam;

		if (be.isDown())
			y += 0.25D;

		setPos(x, y, z);
		setRot(be.getInitialYRotation(), be.getInitialXRotation());
		setZoomAmount(be.getInitialZoom());
	}

	@Override
	protected boolean repositionEntityAfterLoad() {
		return false;
	}

	@Override
	public void tick() {
		if (!level.isClientSide && level.getBlockState(blockPosition()).getBlock() != SCContent.SECURITY_CAMERA.get())
			remove();
	}

	public float getZoomAmount() {
		return entityData.get(ZOOM_AMOUNT);
	}

	public void setZoomAmount(float zoomAmount) {
		entityData.set(ZOOM_AMOUNT, zoomAmount);
	}

	public boolean isCameraDown() {
		return getBlockEntity() != null && !be.isRemoved() && be.isDown();
	}

	public void setRotation(float yaw, float pitch) {
		setRot(yaw, pitch);
	}

	public void setChunkLoadingDistance(int chunkLoadingDistance) {
		initialChunkLoadingDistance = chunkLoadingDistance;
	}

	public boolean hasSentChunks() {
		return hasSentChunks;
	}

	public void setHasSentChunks(boolean hasSentChunks) {
		this.hasSentChunks = hasSentChunks;
	}

	public static boolean hasRecentlyDismounted(PlayerEntity player) {
		return DISMOUNTED_PLAYERS.remove(player);
	}

	@Override
	public void remove() {
		super.remove();
		discardCamera();
	}

	public void stopViewing(ServerPlayerEntity player) {
		if (!level.isClientSide) {
			remove();
			player.camera = player;
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new SetCameraView(player));
			DISMOUNTED_PLAYERS.add(player);

			if (player.getEffect(Effects.NIGHT_VISION) instanceof CameraNightVisionEffectInstance)
				player.removeEffect(Effects.NIGHT_VISION);
		}
	}

	/**
	 * @deprecated Prefer calling {@link #remove()}
	 */
	@Deprecated
	public void discardCamera() {
		if (!level.isClientSide) {
			if (getBlockEntity() != null && !be.isRemoved())
				be.stopViewing();

			SectionPos chunkPos = SectionPos.of(blockPosition());
			int chunkLoadingDistance = initialChunkLoadingDistance <= 0 ? level.getServer().getPlayerList().getViewDistance() : initialChunkLoadingDistance;

			for (int x = chunkPos.getX() - chunkLoadingDistance; x <= chunkPos.getX() + chunkLoadingDistance; x++) {
				for (int z = chunkPos.getZ() - chunkLoadingDistance; z <= chunkPos.getZ() + chunkLoadingDistance; z++) {
					ForgeChunkManager.forceChunk((ServerWorld) level, SecurityCraft.MODID, this, x, z, false, false);
				}
			}
		}
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(ZOOM_AMOUNT, 1.0F);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT tag) {
		tag.putFloat("zoom_amount", getZoomAmount());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT tag) {
		entityData.set(ZOOM_AMOUNT, tag.getFloat("zoom_amount"));
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public SecurityCameraBlockEntity getBlockEntity() {
		if (be == null) {
			TileEntity te = level.getBlockEntity(blockPosition());

			if (te instanceof SecurityCameraBlockEntity)
				be = (SecurityCameraBlockEntity) te;
			else
				SecurityCraft.LOGGER.warn("No security camera block entity was found at {}. Try breaking and replacing the block!", blockPosition());
		}

		return be;
	}
}
