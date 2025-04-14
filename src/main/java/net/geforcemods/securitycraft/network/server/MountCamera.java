package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MountCamera(BlockPos pos) implements CustomPacketPayload {
	public static final Type<MountCamera> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "mount_camera"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, MountCamera> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, MountCamera::pos,
			MountCamera::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();
		BlockState state = level.getBlockState(pos);

		if (!player.isSpectator()) {
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
}
