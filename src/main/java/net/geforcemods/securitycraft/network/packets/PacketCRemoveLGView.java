package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCRemoveLGView implements IMessage{

	private int camDim;
	private BlockPos camPos;

	public PacketCRemoveLGView(){

	}

	public PacketCRemoveLGView(BlockPos camPos, int camDim){
		this.camPos = camPos;
		this.camDim = camDim;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		camPos = BlockPos.fromLong(buf.readLong());
		camDim = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(camPos.toLong());
		buf.writeInt(camDim);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCRemoveLGView, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCRemoveLGView packet, MessageContext ctx) {
			WorldUtils.addScheduledTask(Minecraft.getMinecraft().world, () -> {
				CameraView view = new CameraView(packet.camPos, packet.camDim);

				if(SecurityCraft.instance.hasViewForCoords(view.toNBTString())){
					SecurityCraft.instance.lookingGlass.cleanupWorldView(SecurityCraft.instance.getViewFromCoords(view.toNBTString()).getView());
					SecurityCraft.instance.removeViewForCoords(view.toNBTString());
				}
			});
			return null;
		}

	}

}
