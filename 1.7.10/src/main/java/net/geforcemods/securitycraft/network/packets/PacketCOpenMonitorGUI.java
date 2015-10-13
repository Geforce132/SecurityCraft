package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.gui.GuiCameraMonitor;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class PacketCOpenMonitorGUI implements IMessage {

	private ItemStack monitor;

	public PacketCOpenMonitorGUI(){}

	public PacketCOpenMonitorGUI(ItemStack monitor){
		this.monitor = monitor;
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		ByteBufUtils.writeItemStack(par1ByteBuf, monitor);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.monitor = ByteBufUtils.readItemStack(par1ByteBuf);
	}

public static class Handler extends PacketHelper implements IMessageHandler<PacketCOpenMonitorGUI, IMessage> {
		
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCOpenMonitorGUI packet, MessageContext context) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiCameraMonitor((ItemCameraMonitor) packet.monitor.getItem(), packet.monitor.getTagCompound()));
		return null;
	}
}

}