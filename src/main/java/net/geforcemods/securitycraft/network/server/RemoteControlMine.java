package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RemoteControlMine implements IMessage {
	private int x, y, z;
	private String stateString;

	public RemoteControlMine() {}

	public RemoteControlMine(int x, int y, int z, String state) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.stateString = state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		stateString = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, stateString);
	}

	public static class Handler implements IMessageHandler<RemoteControlMine, IMessage> {
		@Override
		public IMessage onMessage(RemoteControlMine message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				World world = player.world;
				BlockPos pos = new BlockPos(message.x, message.y, message.z);
				IBlockState state = world.getBlockState(pos);

				if (!player.isSpectator() && state.getBlock() instanceof IExplosive) {
					IExplosive explosive = ((IExplosive) state.getBlock());
					TileEntity te = world.getTileEntity(pos);

					if (!(te instanceof IOwnable) || ((IOwnable) te).isOwnedBy(player)) {
						if (message.stateString.equalsIgnoreCase("activate"))
							explosive.activateMine(world, pos);
						else if (message.stateString.equalsIgnoreCase("defuse"))
							explosive.defuseMine(world, pos);
						else if (message.stateString.equalsIgnoreCase("detonate"))
							explosive.explode(world, pos);
					}
				}
			});

			return null;
		}
	}
}
