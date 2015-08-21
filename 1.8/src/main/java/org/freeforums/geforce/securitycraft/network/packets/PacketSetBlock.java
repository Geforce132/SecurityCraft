package org.freeforums.geforce.securitycraft.network.packets;

import org.freeforums.geforce.securitycraft.api.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetBlock implements IMessage{
	
	private int x, y, z, meta;
	private String blockID;
	
	public PacketSetBlock(){
		
	}
	
	public PacketSetBlock(int x, int y, int z, String id, int meta){
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockID = id;
		this.meta = meta;
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, blockID);
		par1ByteBuf.writeInt(meta);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.blockID = ByteBufUtils.readUTF8String(par1ByteBuf);
		this.meta = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetBlock, IMessage> {
	//TODO
	public IMessage onMessage(PacketSetBlock packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		BlockPos pos = BlockUtils.toPos(x, y, z);
		String blockID = packet.blockID;
		int meta = packet.meta;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;
	
		ItemStack[] modules = null;
		ItemStack[] inventory = null;
		int[] times = new int[4];
		String password = "";
		String ownerUUID = "";
		String ownerName = "";

		if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof CustomizableSCTE){
			modules = ((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).itemStacks;
		}
		
		if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadFurnace){
			inventory = ((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).furnaceItemStacks;
			times[0] = ((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).furnaceBurnTime;
			times[1] = ((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).currentItemBurnTime;
			times[2] = ((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).cookTime;
			times[3] = ((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).totalCookTime;
		}
		
		if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityOwnable && ((TileEntityOwnable) getWorld(par1EntityPlayer).getTileEntity(pos)).getOwnerUUID() != null){
			ownerUUID = ((TileEntityOwnable) getWorld(par1EntityPlayer).getTileEntity(pos)).getOwnerUUID();
			ownerName = ((TileEntityOwnable) getWorld(par1EntityPlayer).getTileEntity(pos)).getOwnerName();
		}
		
		if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadChest && ((TileEntityKeypadChest) getWorld(par1EntityPlayer).getTileEntity(pos)).getPassword() != null){
			password = ((TileEntityKeypadChest) getWorld(par1EntityPlayer).getTileEntity(BlockUtils.toPos(x, y, z))).getPassword();
		}
		
		Block block = (Block)Block.blockRegistry.getObject(blockID);
		getWorld(par1EntityPlayer).setBlockState(pos, block.getStateFromMeta(meta));
		
		if(modules != null){
			((CustomizableSCTE) getWorld(par1EntityPlayer).getTileEntity(pos)).itemStacks = modules;
		}
		
		if(inventory != null && getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadFurnace){
			((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).furnaceItemStacks = inventory;
			((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).furnaceBurnTime = times[0];
			((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).currentItemBurnTime = times[1];
			((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).cookTime = times[2];
			((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).totalCookTime = times[3];
		}
		
		if(!ownerName.isEmpty() && !ownerUUID.isEmpty()){
			((TileEntityOwnable) getWorld(par1EntityPlayer).getTileEntity(pos)).setOwner(ownerUUID, ownerName);
		}
		
		if(!password.isEmpty() && getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest) getWorld(par1EntityPlayer).getTileEntity(pos)).setPassword(password);
		}
		
		return null;
	}
}
	
}
