package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetKeycardLevel implements IMessage{
	
	private int x, y, z, level;
	private boolean exactCard;
	
	public PacketSetKeycardLevel(){
		
	}
	
	public PacketSetKeycardLevel(int x, int y, int z, int level, boolean exactCard){
		this.x = x;
		this.y = y;
		this.z = z;
		this.level = level;
		this.exactCard  = exactCard;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(level);
		par1ByteBuf.writeBoolean(exactCard);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.level = par1ByteBuf.readInt();
		this.exactCard = par1ByteBuf.readBoolean();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetKeycardLevel, IMessage> {

	@Override
	public IMessage onMessage(PacketSetKeycardLevel packet, MessageContext context) {
		BlockPos pos = BlockUtils.toPos(packet.x, packet.y, packet.z);
		int level = packet.level;
		boolean exactCard = packet.exactCard;
		EntityPlayer par1EntityPlayer = context.getServerHandler().player;

		((TileEntityKeycardReader) getWorld(par1EntityPlayer).getTileEntity(pos)).setPassword(String.valueOf(level));
		((TileEntityKeycardReader) getWorld(par1EntityPlayer).getTileEntity(pos)).setRequiresExactKeycard(exactCard);
		
		return null;
	}
}

	
}
