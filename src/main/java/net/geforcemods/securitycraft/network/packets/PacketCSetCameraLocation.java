package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCSetCameraLocation implements IMessage{

	private int camDim;
	private BlockPos pos;
	private BlockPos camPos;

	public PacketCSetCameraLocation(){

	}

	public PacketCSetCameraLocation(BlockPos pos, BlockPos camPos, int camDim){
		this.pos = pos;
		this.camPos = camPos;
		this.camDim = camDim;
	}

	@Override
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeLong(pos.toLong());
		par1ByteBuf.writeLong(camPos.toLong());
		par1ByteBuf.writeInt(camDim);
	}

	@Override
	public void fromBytes(ByteBuf par1ByteBuf) {
		pos = BlockPos.fromLong(par1ByteBuf.readLong());
		camPos = BlockPos.fromLong(par1ByteBuf.readLong());
		camDim = par1ByteBuf.readInt();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCSetCameraLocation, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCSetCameraLocation packet, MessageContext context) {
			BlockPos pos = packet.pos;
			BlockPos camPos = packet.camPos;
			int camDim = packet.camDim;

			if(BlockUtils.getBlock(Minecraft.getMinecraft().world, pos) == SCContent.frame){
				((TileEntityFrame) Minecraft.getMinecraft().world.getTileEntity(pos)).setCameraLocation(camPos, camDim);
				((TileEntityFrame) Minecraft.getMinecraft().world.getTileEntity(pos)).enableView();
			}

			return null;
		}
	}

}
