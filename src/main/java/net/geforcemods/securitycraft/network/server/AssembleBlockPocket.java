package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AssembleBlockPocket implements IMessage {
	private BlockPos pos;
	private int dimension, size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerBlockEntity be) {
		pos = be.getPos();
		dimension = be.getWorld().provider.getDimension();
		size = be.getSize();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
		size = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
		buf.writeInt(size);
	}

	public static class Handler implements IMessageHandler<AssembleBlockPocket, IMessage> {
		@Override
		public IMessage onMessage(AssembleBlockPocket message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				TileEntity te = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension).getTileEntity(message.pos);
				EntityPlayer player = ctx.getServerHandler().player;

				if (!player.isSpectator() && te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(player)) {
					TextComponentTranslation feedback;

					((BlockPocketManagerBlockEntity) te).setSize(message.size);
					feedback = ((BlockPocketManagerBlockEntity) te).autoAssembleMultiblock();

					if (feedback != null)
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.blockPocketManager), feedback, TextFormatting.DARK_AQUA);
				}
			});

			return null;
		}
	}
}
