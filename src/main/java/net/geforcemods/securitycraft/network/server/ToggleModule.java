package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ToggleModule implements IMessage {
	private BlockPos pos;
	private EnumModuleType moduleType;

	public ToggleModule() {}

	public ToggleModule(BlockPos pos, EnumModuleType moduleType) {
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
		moduleType = EnumModuleType.values()[ByteBufUtils.readVarInt(buf, 5)];
	}

	public static class Handler implements IMessageHandler<ToggleModule, IMessage> {
		@Override
		public IMessage onMessage(ToggleModule message, MessageContext ctx) {
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = message.pos;
				EntityPlayer player = ctx.getServerHandler().player;
				TileEntity be = player.world.getTileEntity(pos);

				if (be instanceof IModuleInventory && (!(be instanceof IOwnable) || ((IOwnable) be).getOwner().isOwner(player))) {
					IModuleInventory moduleInv = (IModuleInventory) be;
					EnumModuleType moduleType = message.moduleType;

					if (moduleInv.isModuleEnabled(moduleType))
						moduleInv.removeModule(moduleType, true);
					else
						moduleInv.insertModule(moduleInv.getModule(moduleType), true);

					if (be instanceof CustomizableSCTE)
						((CustomizableSCTE) be).sync();
				}
			});

			return null;
		}
	}
}
