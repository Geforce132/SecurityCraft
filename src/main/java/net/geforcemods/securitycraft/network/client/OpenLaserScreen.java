package net.geforcemods.securitycraft.network.client;

import java.util.EnumMap;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.screen.LaserScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OpenLaserScreen implements IMessage {
	private BlockPos pos;
	private NBTTagCompound sideConfig;

	public OpenLaserScreen() {}

	public OpenLaserScreen(BlockPos pos, EnumMap<EnumFacing, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeTag(buf, sideConfig);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		sideConfig = ByteBufUtils.readTag(buf);
	}

	public static class Handler implements IMessageHandler<OpenLaserScreen, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(OpenLaserScreen message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);

				if (te instanceof LaserBlockBlockEntity)
					Minecraft.getMinecraft().displayGuiScreen(new LaserScreen((LaserBlockBlockEntity) te, LaserBlockBlockEntity.loadSideConfig(message.sideConfig)));
			});
			return null;
		}
	}
}
