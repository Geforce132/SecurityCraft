package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetKeycardLevel {

	private int x, y, z, level;
	private boolean exactCard;

	public SetKeycardLevel(){

	}

	public SetKeycardLevel(int x, int y, int z, int level, boolean exactCard){
		this.x = x;
		this.y = y;
		this.z = z;
		this.level = level;
		this.exactCard  = exactCard;
	}

	public static void encode(SetKeycardLevel message, PacketBuffer buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeInt(message.level);
		buf.writeBoolean(message.exactCard);
	}

	public static SetKeycardLevel decode(PacketBuffer buf)
	{
		SetKeycardLevel message = new SetKeycardLevel();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.level = buf.readInt();
		message.exactCard = buf.readBoolean();
		return message;
	}

	public static void onMessage(SetKeycardLevel message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			PlayerEntity player = ctx.get().getSender();
			TileEntity te = player.world.getTileEntity(pos);

			if(te instanceof KeycardReaderTileEntity && ((KeycardReaderTileEntity)te).getOwner().isOwner(player))
			{
				((KeycardReaderTileEntity)te).setPassword(String.valueOf(message.level));
				((KeycardReaderTileEntity)te).setRequiresExactKeycard(message.exactCard);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
