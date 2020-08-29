package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.imc.lookingglass.LookingGlassAPIProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCCreateLGView implements IMessage{

	private int dimension;
	private BlockPos camPos;

	public PacketCCreateLGView(){

	}

	public PacketCCreateLGView(BlockPos camPos, int dimension){
		this.camPos = camPos;
		this.dimension = dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		camPos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(camPos.toLong());
		buf.writeInt(dimension);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCCreateLGView, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCCreateLGView packet, MessageContext ctx) {
			if(!SecurityCraft.instance.hasViewForCoords(packet.camPos.getX() + " " + packet.camPos.getY() + " " + packet.camPos.getZ() + " " + packet.dimension))
				//((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.put(packet.camX + " " + packet.camY + " " + packet.camZ, new IWorldViewHelper(lgView));
				LookingGlassAPIProvider.createLookingGlassView(Minecraft.getMinecraft().world, packet.dimension, packet.camPos, 192, 192);

			return null;
		}

	}

}
