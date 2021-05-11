package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncKeycardSettings
{
	private BlockPos pos;
	private int signature;
	private boolean[] acceptedLevels;

	public SyncKeycardSettings() {}

	public SyncKeycardSettings(BlockPos pos, boolean[] acceptedLevels, int signature)
	{
		this.pos = pos;
		this.acceptedLevels = acceptedLevels;
		this.signature = signature;
	}

	public static void encode(SyncKeycardSettings message, PacketBuffer buf)
	{
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.signature);

		for(int i = 0; i < 5; i++)
		{
			buf.writeBoolean(message.acceptedLevels[i]);
		}
	}

	public static SyncKeycardSettings decode(PacketBuffer buf)
	{
		SyncKeycardSettings message = new SyncKeycardSettings();

		message.pos = buf.readBlockPos();
		message.signature = buf.readVarInt();
		message.acceptedLevels = new boolean[5];

		for(int i = 0; i < 5; i++)
		{
			message.acceptedLevels[i] = buf.readBoolean();
		}

		return message;
	}

	public static void onMessage(SyncKeycardSettings message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = message.pos;
			PlayerEntity player = ctx.get().getSender();
			TileEntity tile = player.world.getTileEntity(pos);

			if(tile instanceof KeycardReaderTileEntity)
			{
				KeycardReaderTileEntity te = (KeycardReaderTileEntity)tile;

				if(te.getOwner().isOwner(player))
				{
					te.setAcceptedLevels(message.acceptedLevels);
					te.setSignature(message.signature);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
