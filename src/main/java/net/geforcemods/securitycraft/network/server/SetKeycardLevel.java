package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(level);
		buf.writeBoolean(exactCard);
	}

	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		level = buf.readInt();
		exactCard = buf.readBoolean();
	}

	public static void encode(SetKeycardLevel message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetKeycardLevel decode(PacketBuffer packet)
	{
		SetKeycardLevel message = new SetKeycardLevel();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetKeycardLevel message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			int level = message.level;
			boolean exactCard = message.exactCard;
			EntityPlayer player = ctx.get().getSender();
			World world = player.world;

			((TileEntityKeycardReader) world.getTileEntity(pos)).setPassword(String.valueOf(level));
			((TileEntityKeycardReader) world.getTileEntity(pos)).setRequiresExactKeycard(exactCard);
		});

		ctx.get().setPacketHandled(true);
	}
}
