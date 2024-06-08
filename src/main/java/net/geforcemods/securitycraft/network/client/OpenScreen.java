package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.screen.AlarmScreen;
import net.geforcemods.securitycraft.screen.RiftStabilizerScreen;
import net.geforcemods.securitycraft.screen.SecureRedstoneInterfaceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OpenScreen implements IMessage {
	private DataType dataType;
	private BlockPos pos;

	public OpenScreen() {}

	public OpenScreen(DataType dataType, BlockPos pos) {
		this.dataType = dataType;
		this.pos = pos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dataType.ordinal());
		buf.writeLong(pos.toLong());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		dataType = DataType.values()[buf.readInt()];
		pos = BlockPos.fromLong(buf.readLong());
	}

	public static class Handler implements IMessageHandler<OpenScreen, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(OpenScreen message, MessageContext context) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				World level = SecurityCraft.proxy.getClientLevel();
				TileEntity te = level.getTileEntity(message.pos);

				switch (message.dataType) {
					case ALARM:
						if (te instanceof AlarmBlockEntity)
							FMLCommonHandler.instance().showGuiScreen(new AlarmScreen((AlarmBlockEntity) te));

						break;
					case RIFT_STABILIZER:
						if (te instanceof RiftStabilizerBlockEntity)
							FMLCommonHandler.instance().showGuiScreen(new RiftStabilizerScreen((RiftStabilizerBlockEntity) te));

						break;
					case SECURE_REDSTONE_INTERFACE:
						if (te instanceof SecureRedstoneInterfaceBlockEntity)
							FMLCommonHandler.instance().showGuiScreen(new SecureRedstoneInterfaceScreen((SecureRedstoneInterfaceBlockEntity) te));

						break;
					default:
						throw new IllegalStateException("Unhandled data type: " + message.dataType.name());
				}
			});

			return null;
		}
	}

	public enum DataType {
		ALARM,
		RIFT_STABILIZER,
		SECURE_REDSTONE_INTERFACE;
	}
}
