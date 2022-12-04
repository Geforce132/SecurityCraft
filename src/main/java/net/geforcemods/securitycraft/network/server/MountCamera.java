package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class MountCamera {
	private BlockPos pos;

	public MountCamera() {}

	public MountCamera(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(MountCamera message, PacketBuffer buf) {
		buf.writeBlockPos(message.pos);
	}

	public static MountCamera decode(PacketBuffer buf) {
		MountCamera message = new MountCamera();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(MountCamera message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			ServerPlayerEntity player = ctx.get().getSender();
			World world = player.level;
			BlockState state = world.getBlockState(pos);

			if (world.isLoaded(pos) && state.getBlock() == SCContent.SECURITY_CAMERA.get()) {
				TileEntity te = world.getBlockEntity(pos);

				if (te instanceof SecurityCameraBlockEntity) {
					SecurityCameraBlockEntity cam = (SecurityCameraBlockEntity) te;

					if (cam.isOwnedBy(player) || cam.isAllowed(player))
						((SecurityCameraBlock) state.getBlock()).mountCamera(world, pos, player);
					else
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", cam.getOwner().getName()), TextFormatting.RED);

					return;
				}
			}

			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", pos), TextFormatting.RED);
		});

		ctx.get().setPacketHandled(true);
	}
}
