package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSCheckPassword implements IMessage{

	private String password;
	private int x, y, z;

	public PacketSCheckPassword(){

	}

	public PacketSCheckPassword(int x, int y, int z, String code){
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, password);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSCheckPassword, IMessage> {

		@Override
		public IMessage onMessage(PacketSCheckPassword message, MessageContext ctx) {
			WorldUtils.addScheduledTask(getWorld(ctx.getServerHandler().player), () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				String password = message.password;
				EntityPlayer player = ctx.getServerHandler().player;

				if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof IPasswordProtected)
					if(((IPasswordProtected) getWorld(player).getTileEntity(pos)).getPassword().equals(password)){
						((EntityPlayerMP) player).closeScreen();
						((IPasswordProtected) getWorld(player).getTileEntity(pos)).activate(player);
					}
			});

			return null;
		}
	}
}
