package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CheckPassword implements IMessage{

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

	public static class Handler implements IMessageHandler<CheckPassword, IMessage> {

		@Override
		public IMessage onMessage(CheckPassword message, MessageContext ctx) {
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = new BlockPos(message.x, message.y, message.z);
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(pos);

				if(te instanceof IPasswordProtected && ((IPasswordProtected)te).getPassword().equals(message.password))
				{
					((EntityPlayerMP) player).closeScreen();
					((IPasswordProtected)te).activate(player);
				}
			});

			return null;
		}
	}
}
