package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetCameraPowered implements IMessage {
	private BlockPos pos;
	private boolean powered;

	public SetCameraPowered() {}

	public SetCameraPowered(BlockPos pos, boolean powered) {
		this.pos = pos;
		this.powered = powered;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(powered);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		powered = buf.readBoolean();
	}

	public static class Handler implements IMessageHandler<SetCameraPowered, IMessage> {
		@Override
		public IMessage onMessage(SetCameraPowered message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(message.pos);

				if (!player.isSpectator() && (te instanceof IOwnable && ((IOwnable) te).isOwnedBy(player)) || (te instanceof IModuleInventory && ((IModuleInventory) te).isAllowed(player))) {
					IBlockState state = world.getBlockState(message.pos);

					world.setBlockState(message.pos, state.withProperty(SecurityCameraBlock.POWERED, message.powered));
					world.notifyNeighborsOfStateChange(message.pos.offset(state.getValue(SecurityCameraBlock.FACING), -1), state.getBlock(), false);
				}
			});
			return null;
		}
	}
}
