package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.client.Minecraft;
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

	public PacketSUpdateTEOwnable() {}

	/**
	 * Initializes this packet with a tile entity
	 * @param te The tile entity to initialize with
	 */
	public PacketSUpdateTEOwnable(TileEntityOwnable te)
	{
		this(te.getPos(), te.getOwner().getName(), te.getOwner().getUUID());
	}

	/**
	 * Initializes this packet
	 * @param p The position of the tile entity
	 * @param sL The amount of stored levels in it
	 */
	public PacketSUpdateTEOwnable(BlockPos p, String n, String id)
	{
		pos = p;
		name = n;
		uuid = id;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, name);
		ByteBufUtils.writeUTF8String(buf, uuid);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		name = ByteBufUtils.readUTF8String(buf);
		uuid = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler implements IMessageHandler<PacketSUpdateTEOwnable, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSUpdateTEOwnable message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> ((TileEntityOwnable)Minecraft.getMinecraft().world.getTileEntity(message.pos)).setOwner(message.uuid, message.name));
			return null;
		}
	}
}
