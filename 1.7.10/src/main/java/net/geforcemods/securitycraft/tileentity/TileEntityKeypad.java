package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityKeypad extends CustomizableSCTE implements IPasswordProtected {
	
	private String passcode;
	
	private OptionBoolean isAlwaysActive = new OptionBoolean("isAlwaysActive", false) {
		public void toggle() {
			super.toggle();
			
			if(getValue()) {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) + 5, 3);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, mod_SecurityCraft.keypad);
			}
			else {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord) - 5, 3);
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, mod_SecurityCraft.keypad);
			}		
		}
	};
    
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
    
    public void activate(EntityPlayer player) {
    	if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypad){
    		BlockKeypad.activate(worldObj, xCoord, yCoord, zCoord);
    	}
	}
    
    public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() == null) {
			player.openGui(mod_SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
		}
		else {
			player.openGui(mod_SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, worldObj, xCoord, yCoord, zCoord);
		}
	}
	
	public boolean onCodebreakerUsed(int meta, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled) {
			PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.keypad.name"), StatCollector.translateToLocal("messages.codebreakerDisabled"), EnumChatFormatting.RED);
		}
		else {
			if(meta > 6 && meta < 11) {
				activate(player);
				return true;
			}
		}
		
		return false;
	}
    
	public String getPassword() {
		return this.passcode;
	}
	
	public void setPassword(String password) {
		passcode = password;
	}

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}

	public Option<?>[] customOptions() {
		return new Option[]{ isAlwaysActive };
	}

}
