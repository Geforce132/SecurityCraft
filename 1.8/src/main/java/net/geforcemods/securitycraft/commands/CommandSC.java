package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class CommandSC extends CommandBase implements ICommand{
	
	private List<String> nicknames;
	
	public CommandSC(){
		this.nicknames = new ArrayList<String>();
		this.nicknames.add("sc");
	}
	
	/**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    
	public String getName() {
		return "sc";
	}
	
	public List<String> getAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender icommandsender) {
		return StatCollector.translateToLocal("messages.command.sc.usage");
	}
	
	public boolean canCommandSenderUse(ICommandSender icommandsender) {
		return true;
	}

	public void execute(ICommandSender icommandsender, String[] par1String) throws CommandException {
		if(par1String.length == 0){
			throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		}
		
		if((par1String[0].matches("connect") || par1String[0].matches("disconnect") || par1String[0].matches("contact") || par1String[0].matches("bug")) && !mod_SecurityCraft.configHandler.isIrcBotEnabled){
			PlayerUtils.sendMessageToPlayer(icommandsender, "IRC", StatCollector.translateToLocal("messages.irc.botDisabled"), EnumChatFormatting.RED);
			return;
		}

		if(par1String.length == 1){
			if(par1String[0].matches("connect")){
								
				try{
					mod_SecurityCraft.instance.getIrcBot(icommandsender.getName()).connectToChannel();
				}catch(Exception e){
					e.printStackTrace();
					PlayerUtils.sendMessageToPlayer(icommandsender, "IRC", StatCollector.translateToLocal("messages.irc.error"), EnumChatFormatting.RED);
					return;
				}
				
				PlayerUtils.sendMessageToPlayer(icommandsender, "IRC", StatCollector.translateToLocal("messages.irc.connected"), EnumChatFormatting.GREEN);
				PlayerUtils.sendMessageToPlayer(icommandsender, "IRC", StatCollector.translateToLocal("messages.irc.info"), EnumChatFormatting.GREEN);
			}else if(par1String[0].matches("disconnect")){
				if(mod_SecurityCraft.instance.getIrcBot(icommandsender.getName()) != null){
					mod_SecurityCraft.instance.getIrcBot(icommandsender.getName()).disconnect();
				}
					
				PlayerUtils.sendMessageToPlayer(icommandsender, "IRC", StatCollector.translateToLocal("messages.irc.disconnected"), EnumChatFormatting.RED);
			}else if(par1String[0].matches("help")){
				getCommandSenderAsPlayer(icommandsender).inventory.addItemStackToInventory(new ItemStack(mod_SecurityCraft.scManual));
			}
			else if(par1String[0].matches("bug"))
				PlayerUtils.sendMessageToPlayer(icommandsender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport").replace("#link", "http://goo.gl/forms/kfRpvvQzfl"), EnumChatFormatting.GOLD);
		}else if(par1String.length >= 2){
			if(par1String[0].matches("contact")){
				if(mod_SecurityCraft.instance.getIrcBot(icommandsender.getName()) != null){
					mod_SecurityCraft.instance.getIrcBot(icommandsender.getName()).sendMessage("#GeforceMods", "> " + getMessageFromArray(par1String, 1));
					sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + icommandsender.getName() + " --> IRC> " + getMessageFromArray(par1String, 1), icommandsender);
				}else{
					PlayerUtils.sendMessageToPlayer(icommandsender, "IRC", StatCollector.translateToLocal("messages.irc.notConnected"), EnumChatFormatting.RED);
				}
			}
			else if(par1String[0].matches("bug"))
				PlayerUtils.sendMessageToPlayer(icommandsender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport").replace("#link", "http://goo.gl/forms/kfRpvvQzfl"), EnumChatFormatting.GOLD);
		}else{
			throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		}
	}

	private static String getMessageFromArray(String[] par1String, int index) {
		String startingString = "";
		for(int i = index; i < par1String.length; i++){
			startingString += (i == index ? "" : " ") + par1String[i];
		}
		
		return startingString;
	}

	private void sendMessageToPlayer(String par1, ICommandSender par2) throws PlayerNotFoundException{
		ChatComponentText chatcomponenttext = new ChatComponentText(par1);
		((EntityPlayerMP) getPlayer(par2, par2.getName())).addChatComponentMessage(chatcomponenttext);
	}
	
	public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}
