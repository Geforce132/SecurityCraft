package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSUpdateCameraRotation implements IMessage
{
	private BlockPos pos;
	private float cameraRotation;
	private boolean addToRotation;

	public PacketSUpdateCameraRotation() {}
	
	/**
	 * Initializes this packet with a tile entity
	 * @param te The tile entity to initialize with
	 */
	public PacketSUpdateCameraRotation(TileEntitySecurityCamera te)
	{
		this(te.getPos(), te.cameraRotation, te.addToRotation);
	}
	
	/**
	 * Initializes this packet
	 * @param p The position of the tile entity
	 * @param sL The amount of stored levels in it
	 */
	public PacketSUpdateCameraRotation(BlockPos p, float rotation, boolean add)
	{
		pos = p;
		cameraRotation = rotation;
		addToRotation = add;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeFloat(cameraRotation);
		buf.writeBoolean(addToRotation);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		cameraRotation = buf.readFloat();
		addToRotation = buf.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSUpdateCameraRotation, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSUpdateCameraRotation message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run()
				{
					TileEntitySecurityCamera te = ((TileEntitySecurityCamera)Minecraft.getMinecraft().theWorld.getTileEntity(message.pos));
					
					te.cameraRotation = message.cameraRotation;
					te.addToRotation = message.addToRotation;
				}
			});
			return null;
		}
	}
}
