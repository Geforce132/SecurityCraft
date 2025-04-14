package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetCameraPowered(BlockPos pos, boolean powered) implements CustomPacketPayload {
	public static final Type<SetCameraPowered> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_camera_powered"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetCameraPowered> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SetCameraPowered::pos,
			ByteBufCodecs.BOOL, SetCameraPowered::powered,
			SetCameraPowered::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		Level level = player.level();
		BlockEntity be = level.getBlockEntity(pos);

		if (!player.isSpectator() && (be instanceof IOwnable ownable && ownable.isOwnedBy(player)) || (be instanceof IModuleInventory moduleInv && moduleInv.isAllowed(player))) {
			BlockState state = level.getBlockState(pos);

			level.setBlockAndUpdate(pos, state.setValue(SecurityCameraBlock.POWERED, powered));
			level.updateNeighborsAt(pos.relative(state.getValue(SecurityCameraBlock.FACING), -1), state.getBlock());
		}
	}
}
