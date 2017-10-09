package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSSyncTENBTTag implements IMessage{
	
	private int x, y, z;
	private NBTTagCompound tag;
	
	public PacketSSyncTENBTTag(){
		
	}
	
	public PacketSSyncTENBTTag(int par1, int par2, int par3, NBTTagCompound par4NBTTagCompound){
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.tag = par4NBTTagCompound;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeTag(buf, this.tag);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSSyncTENBTTag, IMessage> {

	@Override
	public IMessage onMessage(PacketSSyncTENBTTag packet, MessageContext ctx) {
		BlockPos pos = BlockUtils.toPos(packet.x, packet.y, packet.z);
		NBTTagCompound tag = packet.tag;
		EntityPlayer player = ctx.getServerHandler().player;
		
		if(getWorld(player).getTileEntity(pos) != null){
			getWorld(player).getTileEntity(pos).readFromNBT(tag);
		}
		
		return null;
	}
	
}

}
