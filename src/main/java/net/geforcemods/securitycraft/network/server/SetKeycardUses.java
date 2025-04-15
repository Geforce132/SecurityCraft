package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class SetKeycardUses {
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

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(uses);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();

		if (!player.isSpectator() && player.level.getBlockEntity(pos) instanceof KeycardReaderBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)) && player.containerMenu instanceof KeycardReaderMenu keycardReaderContainer)
			keycardReaderContainer.setKeycardUses(uses);
	}
}
