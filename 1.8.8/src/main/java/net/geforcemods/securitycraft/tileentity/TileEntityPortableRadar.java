package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityPortableRadar extends CustomizableSCTE {
		
	private OptionInt searchRadiusOption = new OptionInt("searchRadius", mod_SecurityCraft.configHandler.portableRadarSearchRadius, 5, 50, 5);
	private OptionInt searchDelayOption = new OptionInt("searchDelay", mod_SecurityCraft.configHandler.portableRadarDelay, 4, 10, 1);
    private OptionBoolean repeatMessageOption = new OptionBoolean("repeatMessage", true);	
	
	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";
	
	//Using TileEntitySCTE.attacks() and the attackEntity() method to check for players. :3
    public boolean attackEntity(Entity entity) {
    	if (entity instanceof EntityPlayer) {
    		BlockPortableRadar.searchForPlayers(worldObj, pos, worldObj.getBlockState(pos));
    		return false;
    	} else {
    		return false;
    	}
    }
    
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
        par1NBTTagCompound.setBoolean("shouldSendNewMessage", shouldSendNewMessage);
        par1NBTTagCompound.setString("lastPlayerName", lastPlayerName);
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("shouldSendNewMessage"))
        {
            this.shouldSendNewMessage = par1NBTTagCompound.getBoolean("shouldSendNewMessage");
        }
        
        if (par1NBTTagCompound.hasKey("lastPlayerName"))
        {
            this.lastPlayerName = par1NBTTagCompound.getString("lastPlayerName");
        }
    }
    
    public boolean shouldSendMessage(EntityPlayer player) {
    	if(!player.getCommandSenderName().matches(lastPlayerName)) {
    		shouldSendNewMessage = true;
    		lastPlayerName = player.getCommandSenderName();
    	}
    	
    	return shouldSendNewMessage || repeatMessageOption.asBoolean();
    }
    
    public void setSentMessage() {
    	shouldSendNewMessage = false;
    }
    
    public boolean canAttack() {
    	return true;
    }
    
    public boolean shouldSyncToClient() {
    	return false;
    }
    
    public double getAttackRange() {
    	return (double) searchRadiusOption.asInteger();
    }
    
    public int getTicksBetweenAttacks() {
    	return searchDelayOption.asInteger() * 20;
    }
    
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.REDSTONE, EnumCustomModules.WHITELIST};
	}

	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption };
	}

}
