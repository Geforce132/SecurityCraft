package org.freeforums.geforce.securitycraft.network.packets;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import io.netty.buffer.ByteBuf;

import java.util.Iterator;

import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.Utils.PlayerUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.timers.ScheduleUpdate;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketCheckKeypadCode implements IMessage{
	
	private int x, y, z;
	private String code;
	
	public PacketCheckKeypadCode(){
		
	}
	
	public PacketCheckKeypadCode(int par1, int par2, int par3, String code){
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.code = code;
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		x = par1ByteBuf.readInt();
		y = par1ByteBuf.readInt();
		z = par1ByteBuf.readInt();
		code = ByteBufUtils.readUTF8String(par1ByteBuf);
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, code);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCheckKeypadCode, IMessage>{

	public IMessage onMessage(PacketCheckKeypadCode packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		String code = packet.code;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;
	
		
		String code1 = "";
		String code2 = "";
		String code3 = "";
		if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityKeypad){
			code1 = ((TileEntityKeypad) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).getKeypadCode();
		}else if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityKeypadChest){
			code2 = ((TileEntityKeypadChest) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).getKeypadCode();
		}else if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityKeypadFurnace){
			code3 = ((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(x, y, z)).getKeypadCode();
		}

		if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityKeypad && code.matches(code1)){
			PlayerUtils.sendMessageToPlayer(par1EntityPlayer, "Passcode entered correctly.", EnumChatFormatting.GREEN);
			new ScheduleUpdate(getWorld(par1EntityPlayer), 3, x, y, z);
			((EntityPlayerMP) par1EntityPlayer).closeScreen();
			return null;
		}else if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityKeypadChest && code.matches(code2)){
			PlayerUtils.sendMessageToPlayer(par1EntityPlayer, "Passcode entered correctly.", EnumChatFormatting.GREEN);
			((EntityPlayerMP) par1EntityPlayer).closeScreen();
			((EntityPlayerMP) par1EntityPlayer).displayGUIChest(getChestInventory(getWorld(par1EntityPlayer), x, y, z)); 
			return null;
		}else if(getWorld(par1EntityPlayer).getTileEntity(x, y, z) instanceof TileEntityKeypadFurnace && code.matches(code3)){
			PlayerUtils.sendMessageToPlayer(par1EntityPlayer, "Passcode entered correctly.", EnumChatFormatting.GREEN);
			((EntityPlayerMP) par1EntityPlayer).closeScreen();
			((EntityPlayerMP) par1EntityPlayer).openGui(mod_SecurityCraft.instance, 16, getWorld(par1EntityPlayer), x, y, z); 
			getWorld(par1EntityPlayer).setBlockMetadataWithNotify(x, y, z, getWorld(par1EntityPlayer).getBlockMetadata(x, y, z) + 5, 3);
			return null;
		}
		
		return null;
	}
	
	private IInventory getChestInventory(World par1World, int par2, int par3, int par4){
		Object object = (TileEntityKeypadChest)par1World.getTileEntity(par2, par3, par4);

        if (object == null)
        {
            return null;
        }
        else if (par1World.isSideSolid(par2, par3 + 1, par4, DOWN))
        {
            return null;
        }
        else if (isOcelotSitting(par1World, par2, par3, par4))
        {
            return null;
        }
        else if (par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.keypadChest && (par1World.isSideSolid(par2 - 1, par3 + 1, par4, DOWN) || isOcelotSitting(par1World, par2 - 1, par3, par4)))
        {
            return null;
        }
        else if (par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.keypadChest && (par1World.isSideSolid(par2 + 1, par3 + 1, par4, DOWN) || isOcelotSitting(par1World, par2 + 1, par3, par4)))
        {
            return null;
        }
        else if (par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.keypadChest && (par1World.isSideSolid(par2, par3 + 1, par4 - 1, DOWN) || isOcelotSitting(par1World, par2, par3, par4 - 1)))
        {
            return null;
        }
        else if (par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.keypadChest && (par1World.isSideSolid(par2, par3 + 1, par4 + 1, DOWN) || isOcelotSitting(par1World, par2, par3, par4 + 1)))
        {
            return null;
        }
        else
        {
            if (par1World.getBlock(par2 - 1, par3, par4) == mod_SecurityCraft.keypadChest)
            {
                object = new InventoryLargeChest("container.chestDouble", (TileEntityChest)par1World.getTileEntity(par2 - 1, par3, par4), (IInventory)object);
            }

            if (par1World.getBlock(par2 + 1, par3, par4) == mod_SecurityCraft.keypadChest)
            {
                object = new InventoryLargeChest("container.chestDouble", (IInventory)object, (TileEntityChest)par1World.getTileEntity(par2 + 1, par3, par4));
            }

            if (par1World.getBlock(par2, par3, par4 - 1) == mod_SecurityCraft.keypadChest)
            {
                object = new InventoryLargeChest("container.chestDouble", (TileEntityChest)par1World.getTileEntity(par2, par3, par4 - 1), (IInventory)object);
            }

            if (par1World.getBlock(par2, par3, par4 + 1) == mod_SecurityCraft.keypadChest)
            {
                object = new InventoryLargeChest("container.chestDouble", (IInventory)object, (TileEntityChest)par1World.getTileEntity(par2, par3, par4 + 1));
            }

            return (IInventory)object;
        }
	}
	
	private static boolean isOcelotSitting(World p_149953_0_, int p_149953_1_, int p_149953_2_, int p_149953_3_)
    {
        Iterator iterator = p_149953_0_.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBox((double)p_149953_1_, (double)(p_149953_2_ + 1), (double)p_149953_3_, (double)(p_149953_1_ + 1), (double)(p_149953_2_ + 2), (double)(p_149953_3_ + 1))).iterator();
        EntityOcelot entityocelot1;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            EntityOcelot entityocelot = (EntityOcelot)iterator.next();
            entityocelot1 = (EntityOcelot)entityocelot;
        }
        while (!entityocelot1.isSitting());

        return true;
    }
	
}

}
