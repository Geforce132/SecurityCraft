package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
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

	public static void encode(CheckPassword message, FriendlyByteBuf buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeUtf(message.password);
	}

	public static CheckPassword decode(FriendlyByteBuf buf)
	{
		CheckPassword message = new CheckPassword();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.password = buf.readUtf(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(CheckPassword message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			String password = message.password;
			Player player = ctx.get().getSender();
			BlockEntity te = player.level.getBlockEntity(pos);

			if(te instanceof IPasswordProtected && ((IPasswordProtected)te).getPassword().equals(password))
			{
				((ServerPlayer) player).closeContainer();
				((IPasswordProtected)te).activate(player);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
