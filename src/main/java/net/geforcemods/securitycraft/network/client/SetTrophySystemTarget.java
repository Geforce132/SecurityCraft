package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetTrophySystemTarget implements IMessage {

	private BlockPos trophyPos;
	private int targetID;

	public SetTrophySystemTarget() {}

	public SetTrophySystemTarget(BlockPos trophyPos, int targetID) {
		this.trophyPos = trophyPos;
		this.targetID = targetID;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(trophyPos.toLong());
		buf.writeInt(targetID);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		trophyPos = BlockPos.fromLong(buf.readLong());
		targetID = buf.readInt();
	}

	public static class Handler implements IMessageHandler<SetTrophySystemTarget, IMessage> {
		@Override
		public IMessage onMessage(SetTrophySystemTarget message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.trophyPos);

				if (te instanceof TileEntityTrophySystem) {
					TileEntityTrophySystem trophySystemTE = (TileEntityTrophySystem)te;
					Entity target = Minecraft.getMinecraft().world.getEntityByID(message.targetID);
					trophySystemTE.setTarget(target);
				}
			});

			return null;
		}
	}
}
