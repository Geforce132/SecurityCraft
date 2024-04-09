package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IEMPAffected;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class SecurityCamera extends Entity implements IEMPAffected {
	protected float zoomAmount = 1F;
	protected boolean zooming = false;
	private Ticket chunkTicket;
	private SecurityCameraBlockEntity be;

	public SecurityCamera(World world) {
		super(world);
		noClip = true;
		forceSpawn = true;
		height = 0.0001F;
		width = 0.0001F;

		if (!world.isRemote)
			isDead = true; //If a new camera entity gets spawned by any means other than the player viewing a camera, it should be removed immediately
	}

	public SecurityCamera(World world, double x, double y, double z) {
		this(world);
		isDead = false; //Do not remove the camera entity if it was spawned by a player viewing a camera

		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

		if (!(te instanceof SecurityCameraBlockEntity)) {
			setDead();
			return;
		}

		be = (SecurityCameraBlockEntity) te;
		x += 0.5D;
		y += 0.5D;
		z += 0.5D;

		if (be.isDown())
			y += 0.25D;

		setPosition(x, y, z);
		setRotation(be.getInitialYRotation(), be.getInitialXRotation());
	}

	@Override
	protected boolean shouldSetPosAfterLoading() {
		return false;
	}

	@Override
	public void onUpdate() {
		if (!world.isRemote && world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() != SCContent.securityCamera)
			setDead();
	}

	public float getZoomAmount() {
		return zoomAmount;
	}

	public boolean isCameraDown() {
		return getBlockEntity() != null && !be.isInvalid() && be.isDown();
	}

	//here to make this method accessible to CameraController
	@Override
	protected void setRotation(float yaw, float pitch) {
		super.setRotation(yaw, pitch);
	}

	@Override
	public void setDead() {
		super.setDead();
		discardCamera();
	}

	public void stopViewing(EntityPlayerMP player) {
		if (!world.isRemote) {
			WorldServer serverWorld = (WorldServer) world;
			BlockPos pos = new BlockPos(posX, posY, posZ);
			Chunk chunk = serverWorld.getChunk(pos);
			ChunkPos chunkPos = chunk.getPos();
			ChunkPos playerChunkPos = new ChunkPos(player.getPosition());
			int viewDistance = player.server.getPlayerList().getViewDistance();

			for (int cx = chunkPos.x - viewDistance; cx <= chunkPos.x + viewDistance; cx++) {
				for (int cz = chunkPos.z - viewDistance; cz <= chunkPos.z + viewDistance; cz++) {
					if (cx >= playerChunkPos.x - viewDistance && cx <= playerChunkPos.x + viewDistance && cz >= playerChunkPos.z - viewDistance && cz <= playerChunkPos.z + viewDistance)
						continue; //Do not remove players from chunks that the player entity is supposed to see

					serverWorld.getPlayerChunkMap().getOrCreateEntry(cx, cz).removePlayer(player);
				}
			}

			serverWorld.getPlayerChunkMap().addPlayer(player);
			player.spectatingEntity = player;
			SecurityCraft.network.sendTo(new SetCameraView(player), player);

			//update which entities the player is tracking to allow for the correct ones to show up
			for (EntityTrackerEntry entry : serverWorld.getEntityTracker().entries) {
				if (entry.getTrackedEntity() != player) {
					entry.trackingPlayers.remove(player); //make sure the entity is always sent to the player
					entry.updatePlayerEntity(player);
				}
			}

			setDead();

			if (player.getActivePotionEffect(MobEffects.NIGHT_VISION) instanceof CameraNightVisionEffectInstance)
				player.removePotionEffect(MobEffects.NIGHT_VISION);
		}
	}

	/**
	 * @deprecated Prefer calling {@link #setDead()}
	 */
	@Deprecated
	public void discardCamera() {
		if (!world.isRemote) {
			if (getBlockEntity() != null && !be.isInvalid())
				be.stopViewing();

			if (chunkTicket != null) {
				ForgeChunkManager.releaseTicket(chunkTicket);
				chunkTicket = null;
			}
		}
	}

	public void setChunkTicket(Ticket chunkTicket) {
		this.chunkTicket = chunkTicket;
	}

	@Override
	public void shutDown() {
		removePassengers();
		setDead();
	}

	@Override
	public boolean isShutDown() {
		return false;
	}

	@Override
	public void setShutDown(boolean shutDown) {}

	@Override
	protected void entityInit() {}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {}

	public SecurityCameraBlockEntity getBlockEntity() {
		if (be == null) {
			TileEntity te = world.getTileEntity(getPosition().down());

			if (te instanceof SecurityCameraBlockEntity)
				be = (SecurityCameraBlockEntity) te;
			else
				SecurityCraft.LOGGER.warn("No security camera block entity was found at {}. Try breaking and replacing the block!", getPosition());
		}

		return be;
	}
}
