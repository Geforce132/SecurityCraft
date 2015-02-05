package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.freeforums.geforce.securitycraft.blocks.BlockRetinalScanner;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRetinalScanner;

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
    			blockInfo[1] = movingobjectposition.getBlockPos().getX();
    			blockInfo[2] = movingobjectposition.getBlockPos().getY();
    			blockInfo[3] = movingobjectposition.getBlockPos().getZ();
    			blockInfo[4] = movingobjectposition.sideHit.getIndex();
    			blockInfo[5] = par1World.getBlockState(movingobjectposition.getBlockPos()).getBlock().getMetaFromState(par1World.getBlockState(movingobjectposition.getBlockPos()));
    			blockInfo[0] = Block.getIdFromBlock(par1World.getBlockState(new BlockPos(blockInfo[1], blockInfo[2], blockInfo[3])).getBlock());

    		}
    	}
    	
    	return blockInfo;
    }
    
    private MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean flag, double reach){
    	float f = 1.0F;
    	float playerPitch = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
    	float playerYaw = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
    	double playerPosX = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * f;
    	double playerPosY = (par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * f + 1.6200000000000001D); //TODO
    	double playerPosZ = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * f;
    	Vec3 vecPlayer = new Vec3(playerPosX, playerPosY, playerPosZ);
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
		
		if(getWorld(par1EntityPlayer).getBlockState(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3])).getBlock() != mod_SecurityCraft.retinalScanner || getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3])) == null || !(getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3])) instanceof TileEntityOwnable) || ((TileEntityOwnable)getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]))).getOwnerUUID() == null){
			return null;
		}
			
		if(posXYZ[5] > 1 && posXYZ[5] < 6 && (((TileEntityOwnable)getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]))).getOwnerUUID().matches(par1EntityPlayer.getGameProfile().getId().toString()) || HelpfulMethods.checkForModule(getWorld(par1EntityPlayer), new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]), par1EntityPlayer, EnumCustomModules.WHITELIST))){
        	String ownerUUID = ((TileEntityRetinalScanner) getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]))).getOwnerUUID();
        	String ownerName = ((TileEntityRetinalScanner) getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]))).getOwnerName();
        	Utils.setBlockProperty(getWorld(par1EntityPlayer), new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]), BlockRetinalScanner.POWERED, true);
        	((TileEntityRetinalScanner) getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]))).setOwner(ownerUUID, ownerName);
			getWorld(par1EntityPlayer).scheduleUpdate(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]), mod_SecurityCraft.retinalScanner, 60);
			
			if(mod_SecurityCraft.eventHandler.getCooldown() <= 0){
				ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("Hello " + par1EntityPlayer.getName() + ".", new Object[0]);
				par1EntityPlayer.addChatComponentMessage(chatcomponenttranslation);
				mod_SecurityCraft.network.sendTo(new PacketCUpdateCooldown(250), (EntityPlayerMP) par1EntityPlayer);
				mod_SecurityCraft.eventHandler.setCooldown(250);
			}	
		}else if(posXYZ[5] > 1 && posXYZ[5] < 6 && !((TileEntityOwnable)getWorld(par1EntityPlayer).getTileEntity(new BlockPos(posXYZ[1], posXYZ[2], posXYZ[3]))).getOwnerUUID().matches(par1EntityPlayer.getGameProfile().getId().toString())){
			if(mod_SecurityCraft.eventHandler.getCooldown() <= 0){
				ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("[" + par1EntityPlayer.getName() + "] Unknown player, denying access...", new Object[0]);
				par1EntityPlayer.addChatComponentMessage(chatcomponenttranslation);
				mod_SecurityCraft.network.sendTo(new PacketCUpdateCooldown(250), (EntityPlayerMP) par1EntityPlayer);
			}
		}
		
		return null;
	}
}

}

	



