package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPocketManagerFailedActivation implements IMessage {
	private BlockPos pos;

	public BlockPocketManagerFailedActivation() {}

	public BlockPocketManagerFailedActivation(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	public static class Handler implements IMessageHandler<BlockPocketManagerFailedActivation, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(BlockPocketManagerFailedActivation message, MessageContext context) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TileEntity be = SecurityCraft.proxy.getClientLevel().getTileEntity(message.pos);

				if (be instanceof BlockPocketManagerBlockEntity)
					((BlockPocketManagerBlockEntity) be).setEnabled(false);
			});

			return null;
		}
	}
}
