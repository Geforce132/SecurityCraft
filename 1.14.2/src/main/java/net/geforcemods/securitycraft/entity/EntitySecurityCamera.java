package net.geforcemods.securitycraft.entity;

import net.geforcemods.securitycraft.ConfigHandler.CommonConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.client.SetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.server.GivePotionEffect;
import net.geforcemods.securitycraft.network.server.SetCameraPowered;
import net.geforcemods.securitycraft.network.server.SetCameraRotation;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class EntitySecurityCamera extends Entity{

	private final double CAMERA_SPEED = CommonConfig.CONFIG.cameraSpeed.get();

	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;

	private double cameraUseX;
	private double cameraUseY;
	private double cameraUseZ;
	private float cameraUseYaw;
	private float cameraUsePitch;

	private int id;
	private int screenshotCooldown = 0;
	private int redstoneCooldown = 0;
	private int toggleNightVisionCooldown = 0;
	private int toggleLightCooldown = 0;
	private boolean shouldProvideNightVision = false;
	private float zoomAmount = 1F;

	private String playerViewingName = null;

	public EntitySecurityCamera(World world){
		super(SCContent.eTypeSecurityCamera, world);
		noClip = true;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, PlayerEntity player){
		this(world);
		blockPosX = (int) x;
		blockPosY = (int) y;
		blockPosZ = (int) z;
		cameraUseX = player.posX;
		cameraUseY = player.posY;
		cameraUseZ = player.posZ;
		cameraUseYaw = player.rotationYaw;
		cameraUsePitch = player.rotationPitch;
		this.id = id;
		playerViewingName = player.getName().getFormattedText();
		setPosition(x + 0.5D, y, z + 0.5D);

		rotationPitch = 30F;

		Direction facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

		if(facing == Direction.NORTH)
			rotationYaw = 180F;
		else if(facing == Direction.WEST)
			rotationYaw = 90F;
		else if(facing == Direction.SOUTH)
			rotationYaw = 0F;
		else if(facing == Direction.EAST)
			rotationYaw = 270F;
		else if(facing == Direction.DOWN)
			rotationPitch = 75;
	}

	public EntitySecurityCamera(World world, double x, double y, double z, int id, EntitySecurityCamera camera){
		this(world);
		blockPosX = (int) x;
		blockPosY = (int) y;
		blockPosZ = (int) z;
		cameraUseX = camera.cameraUseX;
		cameraUseY = camera.cameraUseY;
		cameraUseZ = camera.cameraUseZ;
		cameraUseYaw = camera.cameraUseYaw;
		cameraUsePitch = camera.cameraUsePitch;
		this.id = id;
		playerViewingName = camera.playerViewingName;
		setPosition(x + 0.5D, y, z + 0.5D);

		rotationPitch = 30.0F;

		Direction facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

		if(facing == Direction.NORTH)
			rotationYaw = 180F;
		else if(facing == Direction.WEST)
			rotationYaw = 90F;
		else if(facing == Direction.SOUTH)
			rotationYaw = 0F;
		else if(facing == Direction.EAST)
			rotationYaw = 270F;
		else if(facing == Direction.DOWN)
			rotationPitch = 75;
	}

	@Override
	public double getMountedYOffset(){
		return -0.75D;
	}

	@Override
	protected boolean shouldSetPosAfterLoading(){
		return false;
	}

	@Override
	public boolean canBeRiddenInWater(Entity rider){
		return false;
	}

	@Override
	public void tick(){
		if(world.isRemote && isBeingRidden()){
			PlayerEntity lowestEntity = (PlayerEntity)getPassengers().get(0);

			if(screenshotCooldown > 0)
				screenshotCooldown -= 1;

			if(redstoneCooldown > 0)
				redstoneCooldown -= 1;

			if(toggleNightVisionCooldown > 0)
				toggleNightVisionCooldown -= 1;

			if(toggleLightCooldown > 0)
				toggleLightCooldown -= 1;

			if(lowestEntity.rotationYaw != rotationYaw){
				lowestEntity.setPositionAndRotation(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ, rotationYaw, rotationPitch);
				lowestEntity.rotationYaw = rotationYaw;
			}

			if(lowestEntity.rotationPitch != rotationPitch)
				lowestEntity.setPositionAndRotation(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ, rotationYaw, rotationPitch);

			checkKeysPressed();

			//workaround until https://github.com/MinecraftForge/MinecraftForge/pull/5610 is merged
			boolean isMiddleDown = ObfuscationReflectionHelper.getPrivateValue(MouseHelper.class, Minecraft.getInstance().mouseHelper, 2);

			if(isMiddleDown && screenshotCooldown == 0){
				screenshotCooldown = 30;
				ClientUtils.takeScreenshot();
				Minecraft.getInstance().world.playSound(new BlockPos(posX, posY, posZ), ForgeRegistries.SOUND_EVENTS.getValue(SCSounds.CAMERASNAP.location), SoundCategory.BLOCKS, 1.0F, 1.0F, true);
			}

			if(getPassengers().size() != 0 && shouldProvideNightVision)
				SecurityCraft.channel.sendToServer(new GivePotionEffect(Effect.getIdFromPotion(ForgeRegistries.POTIONS.getValue(new ResourceLocation("night_vision"))), 3, -1));
		}

		if(!world.isRemote)
			if(getPassengers().size() == 0 | BlockUtils.getBlock(world, blockPosX, blockPosY, blockPosZ) != SCContent.securityCamera){
				remove();
				return;
			}
	}

	private void checkKeysPressed() {
		if (Minecraft.getInstance().gameSettings.keyBindSneak.isPressed())
			stopRiding();

		if(Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown())
			moveViewUp();

		if(Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown())
			moveViewDown();

		if(Minecraft.getInstance().gameSettings.keyBindLeft.isKeyDown())
			moveViewLeft();

		if(Minecraft.getInstance().gameSettings.keyBindRight.isKeyDown())
			moveViewRight();

		if(KeyBindings.cameraEmitRedstone.isPressed() && redstoneCooldown == 0){
			setRedstonePower();
			redstoneCooldown = 30;
		}

		if(KeyBindings.cameraActivateNightVision.isPressed() && toggleNightVisionCooldown == 0)
			enableNightVision();

		if(KeyBindings.cameraZoomIn.isPressed())
			zoomCameraView(-1);

		if(KeyBindings.cameraZoomOut.isPressed())
			zoomCameraView(1);
	}

	public void moveViewUp() {
		if(isCameraDown())
		{
			if(rotationPitch > 40F)
				setRotation(rotationYaw, rotationPitch -= CAMERA_SPEED);
		}
		else if(rotationPitch > -25F)
			setRotation(rotationYaw, rotationPitch -= CAMERA_SPEED);

		updateServerRotation();
	}

	public void moveViewDown(){
		if(isCameraDown())
		{
			if(rotationPitch < 100F)
				setRotation(rotationYaw, rotationPitch += CAMERA_SPEED);
		}
		else if(rotationPitch < 60F)
			setRotation(rotationYaw, rotationPitch += CAMERA_SPEED);

		updateServerRotation();
	}

	public void moveViewLeft() {
		if(BlockUtils.hasBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING)) {
			Direction facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

			if(facing == Direction.EAST)
			{
				if((rotationYaw - CAMERA_SPEED) > -180F)
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.WEST)
			{
				if((rotationYaw - CAMERA_SPEED) > 0F)
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.NORTH)
			{
				// Handles some problems the occurs from the way the rotationYaw value works in MC
				if((((rotationYaw - CAMERA_SPEED) > 90F) && ((rotationYaw - CAMERA_SPEED) < 185F)) || (((rotationYaw - CAMERA_SPEED) > -190F) && ((rotationYaw - CAMERA_SPEED) < -90F)))
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.SOUTH)
			{
				if((rotationYaw - CAMERA_SPEED) > -90F)
					setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.DOWN)
				setRotation(rotationYaw -= CAMERA_SPEED, rotationPitch);

			updateServerRotation();
		}
	}

	public void moveViewRight(){
		if(BlockUtils.hasBlockProperty(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING)) {
			Direction facing = BlockUtils.getBlockPropertyAsEnum(world, BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ)), BlockSecurityCamera.FACING);

			if(facing == Direction.EAST)
			{
				if((rotationYaw + CAMERA_SPEED) < 0F)
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.WEST)
			{
				if((rotationYaw + CAMERA_SPEED) < 180F)
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.NORTH)
			{
				if((((rotationYaw + CAMERA_SPEED) > 85F) && ((rotationYaw + CAMERA_SPEED) < 185F)) || ((rotationYaw + CAMERA_SPEED) < -95F) && ((rotationYaw + CAMERA_SPEED) > -180F))
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.SOUTH)
			{
				if((rotationYaw + CAMERA_SPEED) < 90F)
					setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);
			}
			else if(facing == Direction.DOWN)
				setRotation(rotationYaw += CAMERA_SPEED, rotationPitch);

			updateServerRotation();
		}
	}

	public void zoomCameraView(int zoom) {
		if(zoom > 0){
			if(zoomAmount == -0.5F)
				zoomAmount = 1F;
			else if(zoomAmount == 1F)
				zoomAmount = 2F;
		}else if(zoom < 0)
			if(zoomAmount == 2F)
				zoomAmount = 1F;
			else if(zoomAmount == 1F)
				zoomAmount = -0.5F;

		Minecraft.getInstance().world.playSound(new BlockPos(posX, posY, posZ), ForgeRegistries.SOUND_EVENTS.getValue(SCSounds.CAMERAZOOMIN.location), SoundCategory.BLOCKS, 1.0F, 1.0F, true);
	}

	public void setRedstonePower() {
		BlockPos pos = BlockUtils.toPos((int) Math.floor(posX), (int) posY, (int) Math.floor(posZ));

		if(((CustomizableSCTE) world.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
			SecurityCraft.channel.sendToServer(new SetCameraPowered(pos, !BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockSecurityCamera.POWERED)));
	}

	public void enableNightVision() {
		toggleNightVisionCooldown = 30;
		shouldProvideNightVision = !shouldProvideNightVision;
	}

	public String getCameraInfo(){
		String nowViewing = TextFormatting.UNDERLINE + "Now viewing camera #" + id + "\n\n";
		String pos = TextFormatting.YELLOW + "Pos: " + TextFormatting.RESET + "X: " + (int) Math.floor(posX) + " Y: " + (int) posY + " Z: " + (int) Math.floor(posZ) + "\n";
		String riderName = getPassengers().get(0).getName().getFormattedText();
		String viewingFrom = (getPassengers().size() != 0 && SecurityCraft.instance.hasUsePosition(riderName)) ? TextFormatting.YELLOW + "Viewing from: " + TextFormatting.RESET + " X: " + (int) Math.floor((Double) SecurityCraft.instance.getUsePosition(riderName)[0]) + " Y: " + (int) Math.floor((Double) SecurityCraft.instance.getUsePosition(riderName)[1]) + " Z: " + (int) Math.floor((Double) SecurityCraft.instance.getUsePosition(riderName)[2]) : "";
		return nowViewing + pos + viewingFrom;
	}

	public float getZoomAmount(){
		return zoomAmount;
	}

	private void updateServerRotation(){
		SecurityCraft.channel.sendToServer(new SetCameraRotation(rotationYaw, rotationPitch));
	}

	private boolean isCameraDown()
	{
		return world.getTileEntity(getPosition()) instanceof TileEntitySecurityCamera && ((TileEntitySecurityCamera)world.getTileEntity(getPosition())).down;
	}

	@Override
	public void remove(){
		super.remove();

		if(playerViewingName != null && PlayerUtils.isPlayerOnline(playerViewingName)){
			PlayerEntity player = PlayerUtils.getPlayerFromName(playerViewingName);
			player.setPositionAndUpdate(cameraUseX, cameraUseY, cameraUseZ);
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SetPlayerPositionAndRotation(cameraUseX, cameraUseY, cameraUseZ, cameraUseYaw, cameraUsePitch));
		}
	}

	@Override
	protected void registerData(){}

	@Override
	public void writeAdditional(CompoundNBT tag){
		tag.putInt("CameraID", id);

		if(playerViewingName != null)
			tag.putString("playerName", playerViewingName);

		if(cameraUseX != 0.0D)
			tag.putDouble("cameraUseX", cameraUseX);

		if(cameraUseY != 0.0D)
			tag.putDouble("cameraUseY", cameraUseY);

		if(cameraUseZ != 0.0D)
			tag.putDouble("cameraUseZ", cameraUseZ);

		if(cameraUseYaw != 0.0D)
			tag.putDouble("cameraUseYaw", cameraUseYaw);

		if(cameraUsePitch != 0.0D)
			tag.putDouble("cameraUsePitch", cameraUsePitch);
	}

	@Override
	public void readAdditional(CompoundNBT tag){
		id = tag.getInt("CameraID");

		if(tag.contains("playerName"))
			playerViewingName = tag.getString("playerName");

		if(tag.contains("cameraUseX"))
			cameraUseX = tag.getDouble("cameraUseX");

		if(tag.contains("cameraUseY"))
			cameraUseY = tag.getDouble("cameraUseY");

		if(tag.contains("cameraUseZ"))
			cameraUseZ = tag.getDouble("cameraUseZ");

		if(tag.contains("cameraUseYaw"))
			cameraUseYaw = tag.getFloat("cameraUseYaw");

		if(tag.contains("cameraUsePitch"))
			cameraUsePitch = tag.getFloat("cameraUsePitch");
	}

}
