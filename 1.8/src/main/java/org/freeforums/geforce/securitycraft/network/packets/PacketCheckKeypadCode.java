package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.freeforums.geforce.securitycraft.blocks.BlockKeypadFurnace;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.timers.ScheduleUpdate;

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
		BlockPos pos = new BlockPos(x, y, z);
		String code = packet.code;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;
	
		
		String code1 = "";
		String code2 = "";
		String code3 = "";
		if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypad){
			code1 = ((TileEntityKeypad) getWorld(par1EntityPlayer).getTileEntity(pos)).getKeypadCode();
		}else if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadChest){
			code2 = ((TileEntityKeypadChest) getWorld(par1EntityPlayer).getTileEntity(pos)).getKeypadCode();
		}else if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadFurnace){
			code3 = ((TileEntityKeypadFurnace) getWorld(par1EntityPlayer).getTileEntity(pos)).getKeypadCode();
		}

		if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypad && code.matches(code1)){
			HelpfulMethods.sendMessageToPlayer(par1EntityPlayer, "Passcode entered correctly.", EnumChatFormatting.GREEN);
			new ScheduleUpdate(getWorld(par1EntityPlayer), 3, x, y, z);
			((EntityPlayerMP) par1EntityPlayer).closeScreen();
			return null;
		}else if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadChest && code.matches(code2)){
			HelpfulMethods.sendMessageToPlayer(par1EntityPlayer, "Passcode entered correctly.", EnumChatFormatting.GREEN);
			((EntityPlayerMP) par1EntityPlayer).closeScreen();
			((EntityPlayerMP) par1EntityPlayer).displayGUIChest(getLockableContainer(getWorld(par1EntityPlayer), new BlockPos(x, y, z))); //((TileEntityKeypadChest) getWorld().getTileEntity(x, y, z))
			return null;
		}else if(getWorld(par1EntityPlayer).getTileEntity(pos) instanceof TileEntityKeypadFurnace && code.matches(code3)){
			HelpfulMethods.sendMessageToPlayer(par1EntityPlayer, "Passcode entered correctly.", EnumChatFormatting.GREEN);
			Utils.setBlockProperty(getWorld(par1EntityPlayer), pos, BlockKeypadFurnace.OPEN, true);
			getWorld(par1EntityPlayer).scheduleUpdate(pos, getWorld(par1EntityPlayer).getBlockState(pos).getBlock(), 60);
			((EntityPlayerMP) par1EntityPlayer).closeScreen();
			return null;
		}
		
		return null;
	}
	
	public ILockableContainer getLockableContainer(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityChest))
        {
            return null;
        }
        else
        {
            Object object = (TileEntityChest)tileentity;

            if (this.isBlocked(worldIn, pos))
            {
                return null;
            }
            else
            {
                Iterator iterator = EnumFacing.Plane.HORIZONTAL.iterator();

                while (iterator.hasNext())
                {
                    EnumFacing enumfacing = (EnumFacing)iterator.next();
                    BlockPos blockpos1 = pos.offset(enumfacing);
                    Block block = worldIn.getBlockState(blockpos1).getBlock();

                    if (block == mod_SecurityCraft.keypadChest)
                    {
                        if (this.isBlocked(worldIn, blockpos1))
                        {
                            return null;
                        }

                        TileEntity tileentity1 = worldIn.getTileEntity(blockpos1);

                        if (tileentity1 instanceof TileEntityChest)
                        {
                            if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH)
                            {
                                object = new InventoryLargeChest("container.chestDouble", (ILockableContainer)object, (TileEntityChest)tileentity1);
                            }
                            else
                            {
                                object = new InventoryLargeChest("container.chestDouble", (TileEntityChest)tileentity1, (ILockableContainer)object);
                            }
                        }
                    }
                }

                return (ILockableContainer)object;
            }
        }
    }
	
	private boolean isBlocked(World worldIn, BlockPos pos)
    {
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
    }

    private boolean isBelowSolidBlock(World worldIn, BlockPos pos)
    {
        return worldIn.isSideSolid(pos.up(), EnumFacing.DOWN, false);
    }
	
	private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos)
    {
        Iterator iterator = worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))).iterator();
        EntityOcelot entityocelot;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            Entity entity = (Entity)iterator.next();
            entityocelot = (EntityOcelot)entity;
        }
        while (!entityocelot.isSitting());

        return true;
    }
	
}

}
