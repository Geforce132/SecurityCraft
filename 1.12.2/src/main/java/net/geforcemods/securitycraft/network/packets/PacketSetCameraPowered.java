package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetCameraPowered implements IMessage
{
	private BlockPos pos;
	private boolean powered;

	public PacketSetCameraPowered() {}

	public PacketSetCameraPowered(BlockPos pos, boolean powered)
	{
		this.pos = pos;
		this.powered = powered;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeBoolean(powered);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		powered = buf.readBoolean();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetCameraPowered, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSetCameraPowered message, MessageContext context)
		{
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				BlockPos pos = message.pos;
				EntityPlayer player = context.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(pos);
				NBTTagCompound modules = ((IModuleInventory) te).writeModuleInventory(new NBTTagCompound());
				Owner owner = ((TileEntityOwnable) te).getOwner();

				world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockSecurityCamera.POWERED, message.powered));
				((IModuleInventory) te).readModuleInventory(modules);
				((TileEntityOwnable) te).getOwner().set(owner.getUUID(), owner.getName());
				world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), false);
			});
			return null;
		}
	}
}
