package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
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
                PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, StatCollector.translateToLocal("tile.retinalScanner.name"), StatCollector.translateToLocal("messages.retinalScanner.hello").replace("#", entity.getName()), EnumChatFormatting.GREEN);
            }             
    	}
	}

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("item.whitelistModule.name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal("module.description.retinalScanner.whitelist")};
	}
}
