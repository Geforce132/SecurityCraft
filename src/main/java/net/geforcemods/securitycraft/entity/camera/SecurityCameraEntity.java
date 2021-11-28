package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.SetCameraView;
import net.geforcemods.securitycraft.network.server.GiveNightVision;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SecurityCameraEntity extends Entity{

	protected final double cameraSpeed = ConfigHandler.CLIENT.cameraSpeed.get();
	public int screenshotSoundCooldown = 0;
	protected int redstoneCooldown = 0;
	protected int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	protected boolean shouldProvideNightVision = false;
	protected float zoomAmount = 1F;
	protected boolean zooming = false;
	private int viewDistance = -1;
	private boolean loadedChunks = false;

	public SecurityCameraEntity(EntityType<SecurityCameraEntity> type, World world){
		super(SCContent.eTypeSecurityCamera, world);
		noClip = true;
		forceSpawn = true;
	}

	public SecurityCameraEntity(World world, BlockPos pos){
		this(SCContent.eTypeSecurityCamera, world);

		TileEntity te = world.getTileEntity(pos);

		if(!(te instanceof SecurityCameraTileEntity))
		{
			remove();
			return;
		}

		SecurityCameraTileEntity cam = (SecurityCameraTileEntity)te;

		double x = pos.getX() + 0.5D;
		double y = pos.getY() + 0.5D;
		double z = pos.getZ() + 0.5D;

		if(cam.down)
			y += 0.25D;

		setPosition(x, y, z);
		setInitialPitchYaw(cam);
	}

	public SecurityCameraEntity(World world, BlockPos pos, SecurityCameraEntity oldCamera){
		this(world, pos);
		oldCamera.discardCamera();
	}

	private void setInitialPitchYaw(SecurityCameraTileEntity te)
	{
		rotationPitch = 30F;

		Direction facing = world.getBlockState(getPosition()).get(SecurityCameraBlock.FACING);

		if(facing == Direction.NORTH)
			rotationYaw = 180F;
		else if(facing == Direction.WEST)
			rotationYaw = 90F;
		else if(facing == Direction.SOUTH)
			rotationYaw = 0F;
		else if(facing == Direction.EAST)
			rotationYaw = 270F;
		else if(facing == Direction.DOWN)
			rotationPitch = 75F;
	}

	@Override
	protected boolean shouldSetPosAfterLoading(){
		return false;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			if(screenshotSoundCooldown > 0)
				screenshotSoundCooldown -= 1;

			if(redstoneCooldown > 0)
				redstoneCooldown -= 1;

			if(toggleNightVisionCooldown > 0)
				toggleNightVisionCooldown -= 1;

			if(toggleLightCooldown > 0)
				toggleLightCooldown -= 1;

			if(getPassengers().size() != 0 && shouldProvideNightVision)
				SecurityCraft.channel.sendToServer(new GiveNightVision());
		}
		else if(world.getBlockState(getPosition()).getBlock() != SCContent.SECURITY_CAMERA.get())
			remove();
	}

	public void toggleRedstonePower() {
		BlockPos pos = getPosition();

		if(((IModuleInventory) world.getTileEntity(pos)).hasModule(ModuleType.REDSTONE))
			SecurityCraft.channel.sendToServer(new SetCameraPowered(pos, !world.getBlockState(pos).get(SecurityCameraBlock.POWERED)));
	}

	public void toggleNightVision() {
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

	public boolean isCameraDown()
	{
		return world.getTileEntity(getPosition()) instanceof SecurityCameraTileEntity && ((SecurityCameraTileEntity)world.getTileEntity(getPosition())).down;
	}

	//here to make this method accessible to CameraController
	@Override
	protected void setRotation(float yaw, float pitch) {
		super.setRotation(yaw, pitch);
	}

	public void stopViewing(ServerPlayerEntity player) {
		if (!world.isRemote) {
			discardCamera();
			player.spectatingEntity = player;
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> player), new SetCameraView(player));
		}
	}

	public void discardCamera() {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(getPosition());

			if(te instanceof SecurityCameraTileEntity)
				((SecurityCameraTileEntity)te).stopViewing();

			SectionPos chunkPos = SectionPos.from(getPosition());
			int viewDistance = this.viewDistance <= 0 ? world.getServer().getPlayerList().getViewDistance() : this.viewDistance;

			for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
				for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
					((ServerWorld)world).forceChunk(x, z, false);
				}
			}
		}

		remove();
	}

	public void setHasLoadedChunks(int initialViewDistance) {
		loadedChunks = true;
		viewDistance = initialViewDistance;
	}

	public boolean hasLoadedChunks() {
		return loadedChunks;
	}

	@Override
	protected void registerData(){}

	@Override
	public void writeAdditional(CompoundNBT tag) {}

	@Override
	public void readAdditional(CompoundNBT tag) {}

	@Override
	public IPacket<?> createSpawnPacket()
	{
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
