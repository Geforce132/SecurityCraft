package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.UniversalBlockReinforcerItem;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncBlockReinforcer implements IMessage {
	private boolean isReinforcing;

	public SyncBlockReinforcer() {}

	public SyncBlockReinforcer(boolean isReinforcing) {
		this.isReinforcing = isReinforcing;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		isReinforcing = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isReinforcing);
	}

	public static class Handler implements IMessageHandler<SyncBlockReinforcer, IMessage> {
		@Override
		public IMessage onMessage(SyncBlockReinforcer message, MessageContext ctx) {
			LevelUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				ItemStack reinforcer = player.inventory.getCurrentItem().getItem() instanceof UniversalBlockReinforcerItem ? player.inventory.getCurrentItem() : player.inventory.offHandInventory.get(0);

				if (!reinforcer.isEmpty() && reinforcer.getItem() != SCContent.universalBlockReinforcerLvL1) {
					if (!reinforcer.hasTagCompound())
						reinforcer.setTagCompound(new NBTTagCompound());

					reinforcer.getTagCompound().setBoolean("is_unreinforcing", !message.isReinforcing);
				}
			});

			return null;
		}
	}
}
