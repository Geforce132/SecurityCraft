package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityDisguisable;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCRefreshDiguisedModel implements IMessage
{
	private BlockPos pos;
	private boolean insert;
	private ItemStack stack;

	public PacketCRefreshDiguisedModel() {}

	public PacketCRefreshDiguisedModel(BlockPos pos, boolean insert, ItemStack stack)
	{
		this.pos = pos;
		this.insert = insert;
		this.stack = stack;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeBoolean(insert);
		ByteBufUtils.writeItemStack(buf, stack);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		insert = buf.readBoolean();
		stack = ByteBufUtils.readItemStack(buf);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCRefreshDiguisedModel,IMessage>
	{
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCRefreshDiguisedModel message, MessageContext context)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TileEntityDisguisable te = (TileEntityDisguisable)Minecraft.getMinecraft().world.getTileEntity(message.pos);

				if(te != null)
				{
					if(message.insert)
						te.insertModule(message.stack);
					else
						te.removeModule(EnumCustomModules.DISGUISE);

					Minecraft.getMinecraft().world.markBlockRangeForRenderUpdate(message.pos, message.pos);
				}
			});
			return null;
		}
	}
}
