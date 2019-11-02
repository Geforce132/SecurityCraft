package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CameraView;

public class PacketCRemoveLGView implements IMessage{

	private int camX, camY, camZ, camDim;

	public PacketCRemoveLGView(){

	}

	public PacketCRemoveLGView(int camX, int camY, int camZ, int camDim){
		this.camX = camX;
		this.camY = camY;
		this.camZ = camZ;
		this.camDim = camDim;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		camX = buf.readInt();
		camY = buf.readInt();
		camZ = buf.readInt();
		camDim = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(camX);
		buf.writeInt(camY);
		buf.writeInt(camZ);
		buf.writeInt(camDim);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCRemoveLGView, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCRemoveLGView packet, MessageContext ctx) {
			CameraView view = new CameraView(packet.camX, packet.camY, packet.camZ, packet.camDim);

			if(SecurityCraft.instance.hasViewForCoords(view.toNBTString())){
				SecurityCraft.instance.getLGPanelRenderer().getApi().cleanupWorldView(SecurityCraft.instance.getViewFromCoords(view.toNBTString()).getView());
				SecurityCraft.instance.removeViewForCoords(view.toNBTString());
			}

			return null;
		}

	}

}
