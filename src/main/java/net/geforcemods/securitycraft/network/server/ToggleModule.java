package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleModule implements IMessage {
	private BlockPos pos;
	private ModuleType moduleType;

	public ToggleModule() {}

	public ToggleModule(BlockPos pos, ModuleType moduleType) {
		this.pos = pos;
		this.moduleType = moduleType;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeVarInt(buf, moduleType.ordinal(), 5);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		moduleType = ModuleType.values()[ByteBufUtils.readVarInt(buf, 5)];
	}

	public static class Handler implements IMessageHandler<ToggleModule, IMessage> {
		@Override
		public IMessage onMessage(ToggleModule message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity be = player.world.getTileEntity(message.pos);

				if (!player.isSpectator() && be instanceof IModuleInventory && (!(be instanceof IOwnable) || ((IOwnable) be).isOwnedBy(player))) {
					IModuleInventory moduleInv = (IModuleInventory) be;

					if (moduleInv.isModuleEnabled(message.moduleType)) {
						moduleInv.removeModule(message.moduleType, true);

						if (be instanceof LinkableBlockEntity) {
							LinkableBlockEntity linkable = (LinkableBlockEntity) be;

							linkable.propagate(new ILinkedAction.ModuleRemoved(message.moduleType, true), linkable);
						}
					}
					else {
						moduleInv.insertModule(moduleInv.getModule(message.moduleType), true);

						if (be instanceof LinkableBlockEntity) {
							LinkableBlockEntity linkable = (LinkableBlockEntity) be;
							ItemStack stack = moduleInv.getModule(message.moduleType);

							linkable.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), true), linkable);
						}
					}

					if (be instanceof CustomizableBlockEntity)
						((CustomizableBlockEntity) be).sync();
				}
			});

			return null;
		}
	}
}
