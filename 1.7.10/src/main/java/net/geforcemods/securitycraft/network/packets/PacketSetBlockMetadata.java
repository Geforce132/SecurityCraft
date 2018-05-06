package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSetBlockMetadata implements IMessage{

	private int x, y, z;
	private int blockMetadata;
	private boolean shouldUpdateBlock;
	private int amountOfTicks;
	private String extraOwnerUUID, extraOwnerName;

	public PacketSetBlockMetadata(){

	}

	public PacketSetBlockMetadata(int x, int y, int z, int meta, boolean shouldUpdate, int amountOfTicks, String extraOwnerUUID, String extraOwnerName){
		this.x = x;
		this.y = y;
		this.z = z;
		blockMetadata = meta;
		shouldUpdateBlock = shouldUpdate;
		this.amountOfTicks = amountOfTicks;
		this.extraOwnerUUID = extraOwnerUUID;
		this.extraOwnerName = extraOwnerName;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(blockMetadata);
		buf.writeBoolean(shouldUpdateBlock);
		buf.writeInt(amountOfTicks);
		ByteBufUtils.writeUTF8String(buf, extraOwnerUUID);
		ByteBufUtils.writeUTF8String(buf, extraOwnerName);

	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		blockMetadata = buf.readInt();
		shouldUpdateBlock = buf.readBoolean();
		amountOfTicks = buf.readInt();
		extraOwnerUUID = ByteBufUtils.readUTF8String(buf);
		extraOwnerName = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetBlockMetadata, IMessage> {

		@Override
		public IMessage onMessage(PacketSetBlockMetadata packet, MessageContext context) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			int blockMetadata = packet.blockMetadata;
			boolean shouldUpdateBlock = packet.shouldUpdateBlock;
			int amountOfTicks = packet.amountOfTicks;
			String extraOwnerUUID = packet.extraOwnerUUID;
			String extraOwnerName = packet.extraOwnerName;
			EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;

			getWorld(par1EntityPlayer).setBlockMetadataWithNotify(x, y, z, blockMetadata, 3);

			if(!extraOwnerUUID.isEmpty() && !extraOwnerName.isEmpty() && getWorld(par1EntityPlayer).getTileEntity(x, y, z) != null)
				if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityOwnable)
					((TileEntityOwnable) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).getOwner().set(extraOwnerUUID, extraOwnerName);

			if(shouldUpdateBlock){
				getWorld(par1EntityPlayer).scheduleBlockUpdate(x, y, z, getWorld(par1EntityPlayer).getBlock(x, y, z), amountOfTicks);
				getWorld(par1EntityPlayer).notifyBlocksOfNeighborChange(x + 1, y, z, getWorld(par1EntityPlayer).getBlock(x, y, z));
				getWorld(par1EntityPlayer).notifyBlocksOfNeighborChange(x - 1, y, z, getWorld(par1EntityPlayer).getBlock(x, y, z));
				getWorld(par1EntityPlayer).notifyBlocksOfNeighborChange(x, y, z + 1, getWorld(par1EntityPlayer).getBlock(x, y, z));
				getWorld(par1EntityPlayer).notifyBlocksOfNeighborChange(x, y, z - 1, getWorld(par1EntityPlayer).getBlock(x, y, z));
			}

			return null;
		}
	}

}
