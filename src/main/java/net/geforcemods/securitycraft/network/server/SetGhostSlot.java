package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.inventory.InventoryScannerMenu;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetGhostSlot implements IMessage {
	private int slotIndex;
	private ItemStack stack;

	public SetGhostSlot() {}

	public SetGhostSlot(int slotIndex, ItemStack stack) {
		this.slotIndex = slotIndex;
		this.stack = stack;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		slotIndex = buf.readInt();
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slotIndex);
		ByteBufUtils.writeItemStack(buf, stack);
	}

	public static class Handler implements IMessageHandler<SetGhostSlot, IMessage> {
		@Override
		public IMessage onMessage(SetGhostSlot message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;

				if (!player.isSpectator() && player.openContainer instanceof InventoryScannerMenu) {
					InventoryScannerMenu menu = (InventoryScannerMenu) player.openContainer;

					if (menu.te.isOwnedBy(player))
						menu.te.getContents().set(message.slotIndex, message.stack);
				}
			});

			return null;
		}
	}
}
