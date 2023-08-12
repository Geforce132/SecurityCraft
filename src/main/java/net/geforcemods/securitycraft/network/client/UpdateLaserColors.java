package net.geforcemods.securitycraft.network.client;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdateLaserColors implements IMessage {
	protected List<BlockPos> positionsToUpdate;

	public UpdateLaserColors() {}

	public UpdateLaserColors(List<BlockPos> positionsToUpdate) {
		this.positionsToUpdate = positionsToUpdate;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, positionsToUpdate.size(), 5);

		for (BlockPos pos : positionsToUpdate) {
			buf.writeLong(pos.toLong());
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int size = ByteBufUtils.readVarInt(buf, 5);

		positionsToUpdate = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			positionsToUpdate.add(BlockPos.fromLong(buf.readLong()));
		}
	}

	public static class Handler implements IMessageHandler<UpdateLaserColors, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(UpdateLaserColors message, MessageContext context) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				for (BlockPos pos : message.positionsToUpdate) {
					SecurityCraft.proxy.updateBlockColorAroundPosition(pos);
				}
			});

			return null;
		}
	}
}
