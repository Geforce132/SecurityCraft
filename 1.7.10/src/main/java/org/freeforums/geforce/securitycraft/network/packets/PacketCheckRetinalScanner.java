package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.Utils.ModuleUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketCheckRetinalScanner implements IMessage{
	
	private String playerName;
	
	public PacketCheckRetinalScanner(){
		
	}
	
	public PacketCheckRetinalScanner(String playerName){
		this.playerName = playerName;
	}
	
	public void fromBytes(ByteBuf par1ByteBuf) {
		this.playerName = ByteBufUtils.readUTF8String(par1ByteBuf);
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		ByteBufUtils.writeUTF8String(par1ByteBuf, playerName);
	}

public static class Handler extends PacketHelper implements IMessageHandler<PacketCheckRetinalScanner, IMessage> {
	
	private int[] getBlockInFront(World par1World, EntityPlayer par2EntityPlayer, double reach){
    	int[] blockInfo = {0, 0, 0, 0, -1, 0};
    	
    	MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(par1World, par2EntityPlayer, true, reach);
    	
    	if(movingobjectposition != null){
    		if(movingobjectposition.typeOfHit == MovingObjectType.BLOCK){
    			blockInfo[1] = movingobjectposition.blockX;
    			blockInfo[2] = movingobjectposition.blockY;
    			blockInfo[3] = movingobjectposition.blockZ;
    			blockInfo[4] = movingobjectposition.sideHit;
    			blockInfo[5] = par1World.getBlockMetadata(blockInfo[1], blockInfo[2], blockInfo[3]);
    			blockInfo[0] = Block.getIdFromBlock(par1World.getBlock(blockInfo[1], blockInfo[2], blockInfo[3]));

    		}
    	}
    	
    	return blockInfo;
    }
    
    private MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean flag, double reach){
    	float f = 1.0F;
    	float playerPitch = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
    	float playerYaw = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
    	double playerPosX = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * f;
    	double playerPosY = (par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * f + 1.6200000000000001D) - par2EntityPlayer.yOffset;
    	double playerPosZ = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * f;
    	Vec3 vecPlayer = Vec3.createVectorHelper(playerPosX, playerPosY, playerPosZ);
    	float cosYaw = MathHelper.cos(-playerYaw * 0.01745329F - 3.141593F);
    	float sinYaw = MathHelper.sin(-playerYaw * 0.01745329F - 3.141593F);
    	float cosPitch = -MathHelper.cos(-playerPitch * 0.01745329F);
    	float sinPitch = -MathHelper.sin(-playerPitch * 0.01745329F);
    	float pointX = sinYaw * cosPitch;
    	float pointY = sinPitch;
    	float pointZ = cosYaw * cosPitch;
    	Vec3 vecPoint = vecPlayer.addVector(pointX * reach, pointY * reach, pointZ * reach);
    	MovingObjectPosition movingobjectposition = par1World.rayTraceBlocks(vecPlayer, vecPoint, flag);
    	return movingobjectposition;

    }

	public IMessage onMessage(PacketCheckRetinalScanner packet, MessageContext context) {
		String playerName = packet.playerName;
		EntityPlayer par1EntityPlayer = context.getServerHandler().playerEntity;
		int[] posXYZ = getBlockInFront(getWorld(par1EntityPlayer), par1EntityPlayer, 1);
		
		if(getWorld(par1EntityPlayer).getBlock(posXYZ[1], posXYZ[2], posXYZ[3]) != mod_SecurityCraft.retinalScanner || getWorld(par1EntityPlayer).getTileEntity(posXYZ[1], posXYZ[2], posXYZ[3]) == null || !(getWorld(par1EntityPlayer).getTileEntity(posXYZ[1], posXYZ[2], posXYZ[3]) instanceof TileEntityOwnable) || ((TileEntityOwnable)getWorld(par1EntityPlayer).getTileEntity(posXYZ[1], posXYZ[2], posXYZ[3])).getOwnerUUID() == null){
			return null;
		}
		
		if(posXYZ[5] > 1 && posXYZ[5] < 6 && (((TileEntityOwnable)getWorld(par1EntityPlayer).getTileEntity(posXYZ[1], posXYZ[2], posXYZ[3])).getOwnerUUID().matches(par1EntityPlayer.getGameProfile().getId().toString()) || ModuleUtils.checkForModule(getWorld(par1EntityPlayer), posXYZ[1], posXYZ[2], posXYZ[3], par1EntityPlayer, EnumCustomModules.WHITELIST))){
			getWorld(par1EntityPlayer).setBlockMetadataWithNotify(posXYZ[1], posXYZ[2], posXYZ[3], posXYZ[5] + 5, 3);
			getWorld(par1EntityPlayer).scheduleBlockUpdate(posXYZ[1], posXYZ[2], posXYZ[3], getWorld(par1EntityPlayer).getBlock(posXYZ[1], posXYZ[2], posXYZ[3]), 60);
			
			if(mod_SecurityCraft.eventHandler.getCooldown() <= 0){
				ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("Hello " + par1EntityPlayer.getCommandSenderName() + ".", new Object[0]);
				par1EntityPlayer.addChatComponentMessage(chatcomponenttranslation);
				mod_SecurityCraft.network.sendTo(new PacketCUpdateCooldown(250), (EntityPlayerMP) par1EntityPlayer);
				mod_SecurityCraft.eventHandler.setCooldown(250);
			}	
		}else if(posXYZ[5] > 1 && posXYZ[5] < 6 && !((TileEntityOwnable)getWorld(par1EntityPlayer).getTileEntity(posXYZ[1], posXYZ[2], posXYZ[3])).getOwnerUUID().matches(par1EntityPlayer.getGameProfile().getId().toString())){
			if(mod_SecurityCraft.eventHandler.getCooldown() <= 0){
				ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("[" + par1EntityPlayer.getCommandSenderName() + "] Unknown player, denying access...", new Object[0]);
				par1EntityPlayer.addChatComponentMessage(chatcomponenttranslation);
				mod_SecurityCraft.network.sendTo(new PacketCUpdateCooldown(250), (EntityPlayerMP) par1EntityPlayer);
			}
		}
		
		return null;
	}
}

}

	



