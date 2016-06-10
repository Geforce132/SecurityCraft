package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
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

	public void fromBytes(ByteBuf buf) {
		this.camX = buf.readInt();
		this.camY = buf.readInt();
		this.camZ = buf.readInt();
		this.camDim = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.camX);
		buf.writeInt(this.camY);
		buf.writeInt(this.camZ);
		buf.writeInt(this.camDim);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCRemoveLGView, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCRemoveLGView packet, MessageContext ctx) {
		CameraView view = new CameraView(packet.camX, packet.camY, packet.camZ, packet.camDim);
		
		if(mod_SecurityCraft.instance.hasViewForCoords(view.toNBTString())){
			mod_SecurityCraft.instance.getLGPanelRenderer().getApi().cleanupWorldView(mod_SecurityCraft.instance.getViewFromCoords(view.toNBTString()).getView());
			mod_SecurityCraft.instance.removeViewForCoords(view.toNBTString());
		}
		
		return null;
	}
	
}

}
