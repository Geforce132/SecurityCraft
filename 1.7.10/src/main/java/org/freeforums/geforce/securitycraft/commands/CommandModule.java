package org.freeforums.geforce.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CommandModule extends CommandBase implements ICommand {

	private List nicknames;

	private final String usage = "Usage: /module add <playerName>";

	public CommandModule() {
		this.nicknames = new ArrayList();
		this.nicknames.add("module");
	}
	
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

	public String getCommandName() {
		return "module";
	}
	
	public List getCommandAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender p_71518_1_) {
		return usage;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	public void processCommand(ICommandSender par1ICommandSender, String[] par2String) {
		if(par2String.length == 2){
			if(par2String[0].matches("add")){
				EntityPlayer player = HelpfulMethods.getPlayerFromName(par1ICommandSender.getCommandSenderName());
				
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){			
					if(player.getCurrentEquippedItem().stackTagCompound == null){
						player.getCurrentEquippedItem().stackTagCompound = new NBTTagCompound();				
					}
					
					player.getCurrentEquippedItem().stackTagCompound.setString("Player" + getNextSlot(player.getCurrentEquippedItem().stackTagCompound), par2String[1]);
					return;
				}
			}
		}
		
		throw new WrongUsageException(usage);
	}

	private int getNextSlot(NBTTagCompound stackTagCompound) {
		for(int i = 1; i <= 10; i++){
			if(stackTagCompound.getString("Player" + i) != null && !stackTagCompound.getString("Player" + i).isEmpty()){
				continue;
			}else{
				return i;
			}
		}
		
		return 0;
	}

	public int compareTo(Object par1Obj) {
        return this.compareTo((ICommand)par1Obj);
	}


}
