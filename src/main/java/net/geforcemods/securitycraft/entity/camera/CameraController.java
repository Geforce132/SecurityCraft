package net.geforcemods.securitycraft.entity.camera;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.misc.SCSounds;
import net.geforcemods.securitycraft.network.server.DismountCamera;
import net.minecraft.block.BlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid=SecurityCraft.MODID, value=Dist.CLIENT)
public class CameraController
{
	public static int previousCameraType;
	private static ClientChunkProvider.ChunkArray cameraStorage;
	private static boolean wasUpPressed;
	private static boolean wasDownPressed;
	private static boolean wasLeftPressed;
	private static boolean wasRightPressed;

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		Entity renderViewEntity = Minecraft.getInstance().renderViewEntity;

		if(renderViewEntity instanceof SecurityCameraEntity)
		{
			SecurityCameraEntity cam = (SecurityCameraEntity)renderViewEntity;
			GameSettings options = Minecraft.getInstance().gameSettings;

			//up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
			if(event.phase == Phase.START)
			{
				if(wasUpPressed = options.keyBindForward.isKeyDown())
					options.keyBindForward.setPressed(false);

				if(wasDownPressed = options.keyBindBack.isKeyDown())
					options.keyBindBack.setPressed(false);

				if(wasLeftPressed = options.keyBindLeft.isKeyDown())
					options.keyBindLeft.setPressed(false);

				if(wasRightPressed = options.keyBindRight.isKeyDown())
					options.keyBindRight.setPressed(false);

				if(options.keyBindSneak.isKeyDown()) {
					dismount();
					options.keyBindSneak.setPressed(false);
				}
			}
			else if(event.phase == Phase.END)
			{
				if(wasUpPressed) {
					moveViewUp(cam);
					options.keyBindForward.setPressed(true);
				}

				if(wasDownPressed) {
					moveViewDown(cam);
					options.keyBindBack.setPressed(true);
				}

				if(wasLeftPressed) {
					moveViewHorizontally(cam, cam.rotationYaw, cam.rotationYaw - (float)cam.cameraSpeed * cam.zoomAmount);
					options.keyBindLeft.setPressed(true);
				}

				if(wasRightPressed) {
					moveViewHorizontally(cam, cam.rotationYaw, cam.rotationYaw + (float)cam.cameraSpeed * cam.zoomAmount);
					options.keyBindRight.setPressed(true);
				}

				if(KeyBindings.cameraZoomIn.isKeyDown())
					zoomIn(cam);
				else if(KeyBindings.cameraZoomOut.isKeyDown())
					zoomOut(cam);
				else
					cam.zooming = false;

				if(KeyBindings.cameraEmitRedstone.isPressed())
					emitRedstone(cam);

				if(KeyBindings.cameraActivateNightVision.isPressed())
					giveNightVision(cam);

				//update other players with the head rotation
				ClientPlayerEntity player = Minecraft.getInstance().player;
				double yRotChange = player.rotationYaw - player.lastReportedYaw;
				double xRotChange = player.rotationPitch - player.lastReportedPitch;

				if(yRotChange != 0.0D || xRotChange != 0.0D)
					player.connection.sendPacket(new CPlayerPacket.RotationPacket(player.rotationYaw, player.rotationPitch, player.onGround));
			}
		}
	}

	private static void dismount()
	{
		SecurityCraft.channel.sendToServer(new DismountCamera());
	}

	public static void moveViewUp(SecurityCameraEntity cam)
	{
		float next = cam.rotationPitch - (float)cam.cameraSpeed * cam.zoomAmount;

		if(cam.isCameraDown())
		{
			if(next > 40F)
				cam.setRotation(cam.rotationYaw, next);
		}
		else if(next > -25F)
			cam.setRotation(cam.rotationYaw, next);
	}

	public static void moveViewDown(SecurityCameraEntity cam)
	{
		float next = cam.rotationPitch + (float)cam.cameraSpeed * cam.zoomAmount;

		if(cam.isCameraDown())
		{
			if(next < 90F)
				cam.setRotation(cam.rotationYaw, next);
		}
		else if(next < 60F)
			cam.setRotation(cam.rotationYaw, next);
	}

	public static void moveViewHorizontally(SecurityCameraEntity cam, float yRot, float next)
	{
		BlockState state = cam.world.getBlockState(cam.getPosition());

		if (state.has(SecurityCameraBlock.FACING)) {
			float checkNext = next;

			if(checkNext < 0)
				checkNext += 360;

			boolean shouldSetRotation = false;

			switch(state.get(SecurityCameraBlock.FACING)) {
				case NORTH: shouldSetRotation = checkNext > 90F && checkNext < 270F; break;
				case SOUTH: shouldSetRotation = checkNext > 270F || checkNext < 90F; break;
				case EAST: shouldSetRotation = checkNext > 180F && checkNext < 360F; break;
				case WEST: shouldSetRotation = checkNext > 0F && checkNext < 180F; break;
				case DOWN: shouldSetRotation = true; break;
				default: shouldSetRotation = false; break;
			}

			if(shouldSetRotation)
				cam.rotationYaw = next;
		}
	}

	public static void zoomIn(SecurityCameraEntity cam)
	{
		if(!cam.zooming)
			Minecraft.getInstance().world.playSound(cam.getPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.max(cam.zoomAmount - 0.1F, 0.1F);
	}

	public static void zoomOut(SecurityCameraEntity cam)
	{
		if(!cam.zooming)
			Minecraft.getInstance().world.playSound(cam.getPosition(), SCSounds.CAMERAZOOMIN.event, SoundCategory.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.min(cam.zoomAmount + 0.1F, 1.4F);
	}

	public static void emitRedstone(SecurityCameraEntity cam)
	{
		if(cam.redstoneCooldown == 0)
		{
			cam.toggleRedstonePower();
			cam.redstoneCooldown = 30;
		}
	}

	public static void giveNightVision(SecurityCameraEntity cam)
	{
		if(cam.toggleNightVisionCooldown == 0)
			cam.toggleNightVision();
	}

	public static ClientChunkProvider.ChunkArray getCameraStorage() {
		return cameraStorage;
	}

	public static void setCameraStorage(ClientChunkProvider.ChunkArray cameraStorage) {
		if (CameraController.cameraStorage == null) {
			CameraController.cameraStorage = cameraStorage;
		}

		if (cameraStorage != null) {
			cameraStorage.centerX = CameraController.cameraStorage.centerX;
			cameraStorage.centerZ = CameraController.cameraStorage.centerZ;

			for(int k = 0; k < CameraController.cameraStorage.chunks.length(); ++k) {
				Chunk chunk = CameraController.cameraStorage.chunks.get(k);

				if (chunk != null) {
					ChunkPos pos = chunk.getPos();

					if (cameraStorage.inView(pos.x, pos.z)) {
						cameraStorage.replace(cameraStorage.getIndex(pos.x, pos.z), chunk);
					}
				}
			}

			CameraController.cameraStorage = cameraStorage;
		}
	}

	public static void setRenderPosition(Entity entity) {
		if (entity instanceof SecurityCameraEntity) {
			SectionPos cameraPos = SectionPos.from(entity);

			cameraStorage.centerX = cameraPos.getSectionX();
			cameraStorage.centerZ = cameraPos.getSectionZ();
		}
	}
}
