package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class RemoveMineFromMRAT implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "remove_mine_from_mrat");
	private int mineIndex;

	public RemoveMineFromMRAT() {}

	public RemoveMineFromMRAT(int mineIndex) {
		this.mineIndex = mineIndex;
	}

	public RemoveMineFromMRAT(FriendlyByteBuf buf) {
		mineIndex = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(mineIndex);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.MINE_REMOTE_ACCESS_TOOL.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundTag tag = stack.getOrCreateTag();

			if (tag.contains("mine" + mineIndex))
				tag.remove("mine" + mineIndex);
		}
	}
}
