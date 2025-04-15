package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetKeycardUses {
	private BlockPos pos;
	private int uses;

	public SetKeycardUses() {}

	public SetKeycardUses(BlockPos pos, int uses) {
		this.pos = pos;
		this.uses = uses;
	}

	public SetKeycardUses(PacketBuffer buf) {
		pos = buf.readBlockPos();
		uses = buf.readVarInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(uses);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof KeycardReaderBlockEntity) {
			KeycardReaderBlockEntity be = (KeycardReaderBlockEntity) te;

			if (be.isOwnedBy(player) || be.isAllowed(player)) {
				Container container = player.containerMenu;

				if (container instanceof KeycardReaderMenu)
					((KeycardReaderMenu) container).setKeycardUses(uses);
			}
		}
	}
}
