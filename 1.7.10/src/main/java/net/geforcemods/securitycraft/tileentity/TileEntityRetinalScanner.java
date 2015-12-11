package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;


public class TileEntityRetinalScanner extends CustomizableSCTE {
	
	public void entityViewed(EntityLivingBase entity) {
		if(!worldObj.isRemote && !BlockUtils.isMetadataBetween(worldObj, xCoord, yCoord, zCoord, 7, 10)){
    		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) + 5, 3);
    		worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, mod_SecurityCraft.retinalScanner, 60);
    		
            if(entity instanceof EntityPlayer){
                PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, StatCollector.translateToLocal("tile.retinalScanner.name"), StatCollector.translateToLocal("messages.retinalScanner.hello").replace("#", entity.getCommandSenderName()), EnumChatFormatting.GREEN);
            }
    	}
	}

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}
	
}
