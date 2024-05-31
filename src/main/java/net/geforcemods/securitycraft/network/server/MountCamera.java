package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

public class MountCamera {
	private BlockPos pos;

	public MountCamera() {}

	public MountCamera(BlockPos pos) {
		this.pos = pos;
	}

	public MountCamera(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		Level level = player.level();
		BlockState state = level.getBlockState(pos);

		if (level.isLoaded(pos) && state.getBlock() == SCContent.SECURITY_CAMERA.get() && level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be && !be.isDisabled() && !be.isShutDown()) {
			if (be.isOwnedBy(player) || be.isAllowed(player))
				((SecurityCameraBlock) state.getBlock()).mountCamera(level, pos, player);
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", be.getOwner().getName()), ChatFormatting.RED);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", pos), ChatFormatting.RED);
	}
}
