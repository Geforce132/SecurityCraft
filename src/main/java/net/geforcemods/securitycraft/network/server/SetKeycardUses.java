package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.containers.KeycardReaderContainer;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

	public static void encode(SetKeycardUses message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.uses);
	}

	public static SetKeycardUses decode(PacketBuffer buf)
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
			PlayerEntity player = ctx.get().getSender();
			TileEntity tile = player.level.getBlockEntity(pos);

			if(tile instanceof KeycardReaderTileEntity)
			{
				KeycardReaderTileEntity te = (KeycardReaderTileEntity)tile;

				if(te.getOwner().isOwner(player) || ModuleUtils.isAllowed(te, player))
				{
					Container container = player.containerMenu;

					if(container instanceof KeycardReaderContainer)
						((KeycardReaderContainer)container).setKeycardUses(message.uses);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
