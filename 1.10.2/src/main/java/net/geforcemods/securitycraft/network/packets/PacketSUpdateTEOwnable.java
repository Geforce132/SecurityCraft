package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSUpdateTEOwnable implements IMessage
{
	private BlockPos pos;
	private String name;
	private String uuid;
	private boolean customizable;
	NBTTagCompound tag;

	public PacketSUpdateTEOwnable() {}

	/**
	 * Initializes this packet with a tile entity
	 * @param te The tile entity to initialize with
	 */
	public PacketSUpdateTEOwnable(TileEntityOwnable te)
	{
		pos = te.getPos();
		name = te.getOwner().getName();
		uuid = te.getOwner().getUUID();
		customizable = te instanceof CustomizableSCTE;

		if(customizable)
			tag = ((CustomizableSCTE)te).writeToNBT(new NBTTagCompound());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, uuid);
		buf.writeBoolean(customizable);

		if(customizable)
			ByteBufUtils.writeTag(buf, tag);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		name = ByteBufUtils.readUTF8String(buf);
		uuid = ByteBufUtils.readUTF8String(buf);
		customizable = buf.readBoolean();

		if(customizable)
			tag = ByteBufUtils.readTag(buf);
	}

	public static class Handler implements IMessageHandler<PacketSUpdateTEOwnable, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSUpdateTEOwnable message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(message.pos);

				if(!(te instanceof TileEntityOwnable))
					return;

				((TileEntityOwnable)te).setOwner(message.uuid, message.name);

				if(message.customizable)
					((CustomizableSCTE)te).readFromNBT(message.tag);
			});
			return null;
		}
	}
}
