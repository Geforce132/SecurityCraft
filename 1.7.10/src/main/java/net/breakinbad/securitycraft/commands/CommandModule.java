package net.breakinbad.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.breakinbad.securitycraft.items.ItemModule;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.main.Utils.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class CommandModule extends CommandBase implements ICommand {

	private List<String> nicknames;

	private final String usage = "/module add <playerName> OR /module remove <playerName> OR /module copy OR /module paste";

	public CommandModule() {
		this.nicknames = new ArrayList<String>();
		this.nicknames.add("module");
	}
	
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

	public String getCommandName() {
		return "module";
	}
	
	public List<String> getCommandAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender p_71518_1_) {
		return usage;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	public void processCommand(ICommandSender par1ICommandSender, String[] par2String) {
		if(par2String.length == 1){
			if(par2String[0].matches("copy")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){		
					mod_SecurityCraft.instance.setSavedModule(player.getCurrentEquippedItem().stackTagCompound);
					PlayerUtils.sendMessageToPlayer(player, "Module data saved.", EnumChatFormatting.GREEN);
				}else{
					PlayerUtils.sendMessageToPlayer(player, "You must be holding the module you wish to save data from.", EnumChatFormatting.RED);
				}
				
				return;
			}else if(par2String[0].matches("paste")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());

				if(mod_SecurityCraft.instance.getSavedModule() == null){
					PlayerUtils.sendMessageToPlayer(player, "There is no module data saved.", EnumChatFormatting.RED);
					return;
				}
				
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){		
					player.getCurrentEquippedItem().stackTagCompound = mod_SecurityCraft.instance.getSavedModule();
					mod_SecurityCraft.instance.setSavedModule(null);
					PlayerUtils.sendMessageToPlayer(player, "Saved data to module.", EnumChatFormatting.GREEN);
				}
				
				return;
			}
		}else if(par2String.length == 2){
			if(par2String[0].matches("add")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());
				
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){			
					if(player.getCurrentEquippedItem().stackTagCompound == null){
						player.getCurrentEquippedItem().stackTagCompound = new NBTTagCompound();				
					}
					
					for(int i = 1; i <= 10; i++){
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).matches(par2String[1])){
							PlayerUtils.sendMessageToPlayer(player, "The module you are holding already contains the player " + par2String[1] + ".", EnumChatFormatting.RED);
							return;
						}
					}
					
					player.getCurrentEquippedItem().stackTagCompound.setString("Player" + getNextSlot(player.getCurrentEquippedItem().stackTagCompound), par2String[1]);
					PlayerUtils.sendMessageToPlayer(player, "Added " + par2String[1] + " to the held module.", EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, "You must be holding the module you wish to modify!", EnumChatFormatting.RED);
					return;
				}
			}else if(par2String[0].matches("remove")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());
				
				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canBeModified()){			
					if(player.getCurrentEquippedItem().getTagCompound() == null){
						player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());				
					}
					
					for(int i = 1; i <= 10; i++){
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).matches(par2String[1])){
							player.getCurrentEquippedItem().getTagCompound().removeTag("Player" + i);
						}
					}
					
					PlayerUtils.sendMessageToPlayer(player, "Removed " + par2String[1] + " from the held module.", EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, "You must be holding the module you wish to modify!", EnumChatFormatting.RED);
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
