package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSSetOwner implements IMessage {

	private int x, y, z;
	private String uuid, name;

	public PacketSSetOwner(){

	}

	public PacketSSetOwner(int x, int y, int z, String uuid, String name){
		this.x = x;
		this.y = y;
		this.z = z;
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		uuid = ByteBufUtils.readUTF8String(buf);
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, uuid);
		ByteBufUtils.writeUTF8String(buf, name);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSetOwner, IMessage>{

		@Override
		public IMessage onMessage(PacketSSetOwner packet, MessageContext ctx) {
			BlockPos pos = BlockUtils.toPos(packet.x, packet.y, packet.z);
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof IOwnable)
				((IOwnable) getWorld(player).getTileEntity(pos)).getOwner().set(packet.uuid, packet.name);

			return null;
		}

	}

}
