package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
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

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeString(password);
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = buf.readString(Integer.MAX_VALUE);
	}

	public static void encode(CheckPassword message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static CheckPassword decode(PacketBuffer packet)
	{
		CheckPassword message = new CheckPassword();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(CheckPassword message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			String password = message.password;
			EntityPlayer player = ctx.get().getSender();

			if(player.world.getTileEntity(pos) != null && player.world.getTileEntity(pos) instanceof IPasswordProtected)
				if(((IPasswordProtected) player.world.getTileEntity(pos)).getPassword().equals(password)){
					((EntityPlayerMP) player).closeScreen();
					((IPasswordProtected) player.world.getTileEntity(pos)).activate(player);
				}
		});

		ctx.get().setPacketHandled(true);
	}
}
