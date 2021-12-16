package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.inventory.KeycardReaderMenu;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SetKeycardUses
{
	private BlockPos pos;
	private int uses;

	public SetKeycardUses() {}

	public SetKeycardUses(BlockPos pos, int uses)
	{
		this.pos = pos;
		this.uses = uses;
	}

	public static void encode(SetKeycardUses message, FriendlyByteBuf buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.uses);
	}

	public static SetKeycardUses decode(FriendlyByteBuf buf)
	{
		SetKeycardUses message = new SetKeycardUses();

		message.pos = buf.readBlockPos();
		message.uses = buf.readVarInt();
		return message;
	}

	public static void onMessage(SetKeycardUses message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			Player player = ctx.get().getSender();

			if(player.level.getBlockEntity(pos) instanceof KeycardReaderBlockEntity be)
			{
				if(be.getOwner().isOwner(player) || ModuleUtils.isAllowed(be, player))
				{
					if(player.containerMenu instanceof KeycardReaderMenu keycardReaderContainer)
						keycardReaderContainer.setKeycardUses(message.uses);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
