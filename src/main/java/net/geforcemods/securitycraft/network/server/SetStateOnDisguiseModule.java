package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCStreamCodecs;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetStateOnDisguiseModule(BlockState state, StandingOrWallType standingOrWall) implements CustomPacketPayload {
	public static final Type<SetStateOnDisguiseModule> TYPE = new Type<>(new ResourceLocation(SecurityCraft.MODID, "set_state_on_disguise_module"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SetStateOnDisguiseModule> STREAM_CODEC = StreamCodec.composite(
			SCStreamCodecs.BLOCK_STATE, SetStateOnDisguiseModule::state,
			NeoForgeStreamCodecs.enumCodec(StandingOrWallType.class), SetStateOnDisguiseModule::standingOrWall,
			SetStateOnDisguiseModule::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.DISGUISE_MODULE.get());

		if (!stack.isEmpty()) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
				if (state.isAir()) {
					tag.remove("SavedState");
					tag.remove("StandingOrWall");
				}
				else {
					tag.put("SavedState", NbtUtils.writeBlockState(state));
					tag.putInt("StandingOrWall", standingOrWall.ordinal());
				}
			});
		}
	}
}
