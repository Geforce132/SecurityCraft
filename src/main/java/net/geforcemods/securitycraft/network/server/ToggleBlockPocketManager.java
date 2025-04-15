package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.network.client.BlockPocketManagerFailedActivation;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleBlockPocketManager implements IMessage {
	private BlockPos pos;
	private int dimension, size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity te, boolean enabling) {
		pos = te.getPos();
		dimension = te.getWorld().provider.getDimension();
		this.enabling = enabling;
		size = te.getSize();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		dimension = buf.readInt();
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeInt(dimension);
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	public static class Handler implements IMessageHandler<ToggleBlockPocketManager, IMessage> {
		@Override
		public IMessage onMessage(ToggleBlockPocketManager message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(player)) {
					TextComponentTranslation feedback;

					((BlockPocketManagerBlockEntity) te).setSize(message.size);

					if (message.enabling)
						feedback = ((BlockPocketManagerBlockEntity) te).enableMultiblock();
					else
						feedback = ((BlockPocketManagerBlockEntity) te).disableMultiblock();

					if (feedback != null) {
						if (message.enabling && !((BlockPocketManagerBlockEntity) te).isEnabled())
							SecurityCraft.network.sendTo(new BlockPocketManagerFailedActivation(message.pos), player);

						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.blockPocketManager), feedback, TextFormatting.DARK_AQUA, false);
					}
				}
			});

			return null;
		}
	}
}
