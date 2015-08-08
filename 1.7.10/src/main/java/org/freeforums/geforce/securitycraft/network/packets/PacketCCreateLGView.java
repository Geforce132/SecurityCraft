package org.freeforums.geforce.securitycraft.network.packets;

import org.freeforums.geforce.securitycraft.imc.lookingglass.CameraAnimatorSecurityCamera;
import org.freeforums.geforce.securitycraft.imc.lookingglass.IWorldViewHelper;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.ClientProxy;

import com.xcompwiz.lookingglass.api.view.IWorldView;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;

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

	public void fromBytes(ByteBuf buf) {
		this.camX = buf.readInt();
		this.camY = buf.readInt();
		this.camZ = buf.readInt();
		this.dimension = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.camX);
		buf.writeInt(this.camY);
		buf.writeInt(this.camZ);
		buf.writeInt(this.dimension);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCCreateLGView, IMessage> {

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCCreateLGView packet, MessageContext ctx) {
		if(!mod_SecurityCraft.instance.hasViewForCoords(packet.camX + " " + packet.camY + " " + packet.camZ)){
			IWorldView lgView = mod_SecurityCraft.instance.getLGPanelRenderer().createWorldView(packet.dimension, new ChunkCoordinates(packet.camX, packet.camY, packet.camZ), 192, 192);
			lgView.setAnimator(new CameraAnimatorSecurityCamera(lgView.getCamera(), Minecraft.getMinecraft().theWorld.getBlockMetadata(packet.camX, packet.camY, packet.camZ)));
			
			((ClientProxy) mod_SecurityCraft.instance.serverProxy).worldViews.put(packet.camX + " " + packet.camY + " " + packet.camZ, new IWorldViewHelper(lgView));		
		}
		
		return null;
	}
	
}

}
