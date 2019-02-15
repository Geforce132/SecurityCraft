package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSAddModules implements IMessage{

	private int x, y, z;
	private ItemStack[] modules;
	private int arrayLength;

	public PacketSAddModules(){

	}

	public PacketSAddModules(int x, int y, int z, ItemStack... modules){
		this.x = x;
		this.y = y;
		this.z = z;
		this.modules = modules;
		arrayLength = modules.length;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(arrayLength);
		for(ItemStack stack : modules)
			ByteBufUtils.writeItemStack(buf, stack);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		arrayLength = buf.readInt();
		for(int i = 0; i < arrayLength; i++){
			if(modules == null)
				modules = new ItemStack[arrayLength];

			modules[i] = ByteBufUtils.readItemStack(buf);
		}
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSAddModules, IMessage> {

		@Override
		public IMessage onMessage(PacketSAddModules message, MessageContext context) {
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				ItemStack[] modules = message.modules;
				EntityPlayer player = context.getServerHandler().player;

				if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof CustomizableSCTE)
					for(ItemStack module : modules)
						if(!((CustomizableSCTE) getWorld(player).getTileEntity(pos)).hasModule(EnumCustomModules.getModuleFromStack(module)))
							((CustomizableSCTE) getWorld(player).getTileEntity(pos)).insertModule(module);
			});

			return null;
		}
	}


}
