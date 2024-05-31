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

	public MountCamera(PacketBuffer buf) {
		pos = buf.readBlockPos();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		World level = player.level;
		BlockState state = level.getBlockState(pos);

		if (level.isLoaded(pos) && state.getBlock() == SCContent.SECURITY_CAMERA.get()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof SecurityCameraBlockEntity) {
				SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;

				if (!be.isDisabled() && !be.isShutDown()) {
					if (be.isOwnedBy(player) || be.isAllowed(player))
						((SecurityCameraBlock) state.getBlock()).mountCamera(level, pos, player);
					else
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", be.getOwner().getName()), TextFormatting.RED);
				}

				return;
			}
		}

		PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", pos), TextFormatting.RED);
	}
}
