package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SetKeycardUses implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "set_keycard_uses");
	private BlockPos pos;
	private int uses;

	public SetKeycardUses() {}

	public SetKeycardUses(BlockPos pos, int uses) {
		this.pos = pos;
		this.uses = uses;
	}

	public SetKeycardUses(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		uses = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(uses);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof KeycardReaderBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)) && player.containerMenu instanceof KeycardReaderMenu keycardReaderContainer)
			keycardReaderContainer.setKeycardUses(uses);
	}
}
