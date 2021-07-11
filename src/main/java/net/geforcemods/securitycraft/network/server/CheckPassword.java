package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class CheckPassword {

	private String password;
	private int x, y, z;

	public CheckPassword(){

	}

	public CheckPassword(int x, int y, int z, String code){
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	public static void encode(CheckPassword message, PacketBuffer buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeString(message.password);
	}

	public static CheckPassword decode(PacketBuffer buf)
	{
		CheckPassword message = new CheckPassword();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.password = buf.readString(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(CheckPassword message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			String password = message.password;
			PlayerEntity player = ctx.get().getSender();
			TileEntity te = player.world.getTileEntity(pos);

			if(te instanceof IPasswordProtected && ((IPasswordProtected)te).getPassword().equals(password))
			{
				((ServerPlayerEntity) player).closeScreen();
				((IPasswordProtected)te).activate(player);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
