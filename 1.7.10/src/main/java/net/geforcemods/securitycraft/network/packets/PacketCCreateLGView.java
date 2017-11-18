package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.imc.lookingglass.LookingGlassAPIProvider;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.Minecraft;

public class PacketCCreateLGView implements IMessage{

	private int camX, camY, camZ, dimension;

	public PacketCCreateLGView(){

	}

	public PacketCCreateLGView(int camX, int camY, int camZ, int dimension){
		this.camX = camX;
		this.camY = camY;
		this.camZ = camZ;
		this.dimension = dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		camX = buf.readInt();
		camY = buf.readInt();
		camZ = buf.readInt();
		dimension = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(camX);
		buf.writeInt(camY);
		buf.writeInt(camZ);
		buf.writeInt(dimension);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCCreateLGView, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCCreateLGView packet, MessageContext ctx) {
			if(!mod_SecurityCraft.instance.hasViewForCoords(packet.camX + " " + packet.camY + " " + packet.camZ + " " + packet.dimension))
				//((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.put(packet.camX + " " + packet.camY + " " + packet.camZ, new IWorldViewHelper(lgView));
				LookingGlassAPIProvider.createLookingGlassView(Minecraft.getMinecraft().theWorld, packet.dimension, packet.camX, packet.camY, packet.camZ, 192, 192);

			return null;
		}

	}

}
