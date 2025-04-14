package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class RemovePositionFromSSS implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "remove_position_from_sss");
	private BlockPos pos;

	public RemovePositionFromSSS() {}

	public RemovePositionFromSSS(BlockPos pos) {
		this.pos = pos;
	}

	public RemovePositionFromSSS(FriendlyByteBuf buf) {
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
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		if (!player.isSpectator() && !stack.isEmpty())
			SonicSecuritySystemItem.removeLinkedBlock(stack.getOrCreateTag(), pos);
	}
}
