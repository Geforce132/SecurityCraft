package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetDefaultCameraViewingDirection implements IMessage {
	private int id;
	private float initialXRotation, initialYRotation, initialZoom;

	public SetDefaultCameraViewingDirection() {}

	public SetDefaultCameraViewingDirection(SecurityCamera cam) {
		id = cam.getEntityId();
		initialXRotation = cam.rotationPitch;
		initialYRotation = cam.rotationYaw;
		initialZoom = cam.getZoomAmount();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		initialXRotation = buf.readFloat();
		initialYRotation = buf.readFloat();
		initialZoom = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeFloat(initialXRotation);
		buf.writeFloat(initialYRotation);
		buf.writeFloat(initialZoom);
	}

	public static class Handler implements IMessageHandler<SetDefaultCameraViewingDirection, IMessage> {
		@Override
		public IMessage onMessage(SetDefaultCameraViewingDirection message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				Entity playerCamera = player.getSpectatingEntity();

				if (!player.isSpectator() && playerCamera.getEntityId() == message.id && playerCamera instanceof SecurityCamera) {
					TileEntity te = playerCamera.world.getTileEntity(new BlockPos(playerCamera.posX, playerCamera.posY, playerCamera.posZ));

					if (te instanceof SecurityCameraBlockEntity) {
						SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;

						if (!be.isOwnedBy(player)) {
							player.sendStatusMessage(Utils.localize("messages.securitycraft:security_camera.no_permission"), true);
							return;
						}

						if (be.isModuleEnabled(ModuleType.SMART)) {
							be.setDefaultViewingDirection(message.initialXRotation, message.initialYRotation, message.initialZoom);
							player.sendStatusMessage(Utils.localize("messages.securitycraft:security_camera.direction_set"), true);
						}
						else
							player.sendStatusMessage(Utils.localize("messages.securitycraft:security_camera.smart_module_needed"), true);
					}
				}
			});

			return null;
		}
	}
}
