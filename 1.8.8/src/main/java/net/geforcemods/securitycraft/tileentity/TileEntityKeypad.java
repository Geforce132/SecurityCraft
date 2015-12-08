package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityKeypad extends CustomizableSCTE implements IPasswordProtected {
		
	private String passcode;
    
    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        if(this.passcode != null && !this.passcode.isEmpty()){
        	par1NBTTagCompound.setString("passcode", this.passcode);
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("passcode"))
        {
        	if(par1NBTTagCompound.getInteger("passcode") != 0){
        		this.passcode = String.valueOf(par1NBTTagCompound.getInteger("passcode"));
        	}else{
        		this.passcode = par1NBTTagCompound.getString("passcode");
        	}
        }
    }

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}

	public String[] getOptionDescriptions() {
		return new String[]{EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("item.whitelistModule.name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal("module.description.keypad.whitelist"),
				EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal("item.blacklistModule.name") + ":" + EnumChatFormatting.RESET + "\n\n" + StatCollector.translateToLocal("module.description.keypad.blacklist")};
		}
	
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypad){
    		BlockKeypad.activate(worldObj, pos);
    	}
	}
	
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() == null) {
			player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
		}
		else {
			player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, pos.getX(), pos.getY(), pos.getZ());
		}
	}
	
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled) {
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);
		}
		else {
			if(!BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockKeypad.POWERED)) {
				activate(player);
				return true;
			}
		}
		
		return false;
	}
	
	public String getPassword() {
		return (this.passcode != null && !this.passcode.isEmpty()) ? this.passcode : null;
	}

	public void setPassword(String password) {
		passcode = password;
	}
	
}
