package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;


public class TileEntityRetinalScanner extends CustomizableSCTE {
	
	public void entityViewed(EntityLivingBase entity){
		if(!worldObj.isRemote && !BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockRetinalScanner.POWERED)){
    		BlockUtils.setBlockProperty(worldObj, pos, BlockRetinalScanner.POWERED, true);
    		worldObj.scheduleUpdate(new BlockPos(pos), mod_SecurityCraft.retinalScanner, 60);
    		
            if(entity instanceof EntityPlayer){
                PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, StatCollector.translateToLocal("tile.retinalScanner.name"), StatCollector.translateToLocal("messages.retinalScanner.hello").replace("#", entity.getCommandSenderName()), EnumChatFormatting.GREEN);
            }             
    	}
	}

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	public Option<?>[] customOptions() {
		return null;
	}

}
