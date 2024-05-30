package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MountCamera implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "mount_camera");
	private BlockPos pos;

	public MountCamera() {}

	public MountCamera(BlockPos pos) {
		this.pos = pos;
	}

	public MountCamera(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();
		BlockState state = level.getBlockState(pos);

		if (level.isLoaded(pos) && state.getBlock() == SCContent.SECURITY_CAMERA.get() && level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be && !be.isDisabled()) {
			if (be.isOwnedBy(player) || be.isAllowed(player))
				((SecurityCameraBlock) state.getBlock()).mountCamera(level, pos, player);
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", be.getOwner().getName()), ChatFormatting.RED);
		}
		else
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CAMERA_MONITOR.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", pos), ChatFormatting.RED);
	}
}
