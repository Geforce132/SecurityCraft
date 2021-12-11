package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetCameraView implements IMessage
{
	private int id;

	public SetCameraView() {}

	public SetCameraView(Entity camera)
	{
		id = camera.getEntityId();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, id, 5);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		id = ByteBufUtils.readVarInt(buf, 5);
	}

	public static class Handler implements IMessageHandler<SetCameraView, IMessage> {
		@Override
		public IMessage onMessage(SetCameraView message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft mc = Minecraft.getMinecraft();
				Entity entity = mc.world.getEntityByID(message.id);
				boolean isCamera = entity instanceof EntitySecurityCamera;

				if(isCamera || entity instanceof EntityPlayer)
				{
					mc.setRenderViewEntity(entity);

					if (isCamera) {
						CameraController.previousCameraType = mc.gameSettings.thirdPersonView;
						mc.gameSettings.thirdPersonView = 0;
						mc.ingameGUI.setOverlayMessage(Utils.localize("mount.onboard", mc.gameSettings.keyBindSneak.getDisplayName()), false);
					}
					else if(CameraController.previousCameraType >= 0 && CameraController.previousCameraType < 3)
						mc.gameSettings.thirdPersonView = CameraController.previousCameraType;

					mc.renderGlobal.loadRenderers();
				}
			});

			return null;
		}
	}
}
