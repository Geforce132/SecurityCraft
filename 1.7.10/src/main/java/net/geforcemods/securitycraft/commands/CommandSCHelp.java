package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandSCHelp extends CommandBase implements ICommand{
	
	private List<String> nicknames;
	
	private final String usage = "Usage: /sc connect OR /sc disconnect OR /sc bug <bug to report> OR /sc contact <message>";
	
	public CommandSCHelp(){
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
    
	public String getCommandName() {
		return "sc";
	}
	
	public List<String> getCommandAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender icommandsender) {
		return usage;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@SuppressWarnings("static-access")
	public void processCommand(ICommandSender icommandsender, String[] par1String) {
		if(par1String.length == 0){
			throw new WrongUsageException(usage);
		}
		
		if((par1String[0].matches("connect") || par1String[0].matches("disconnect") || par1String[0].matches("contact") || par1String[0].matches("bug")) && !mod_SecurityCraft.instance.configHandler.isIrcBotEnabled){
			sendMessageToPlayer("The SecurityCraft IRC bot is disabled from the config file. Please enable to it to use this feature.", icommandsender);
			return;
		}
		
		if(par1String.length == 1){
			if(par1String[0].matches("connect")){
								
				try{
					mod_SecurityCraft.instance.getIrcBot(icommandsender.getCommandSenderName()).connectToChannel();
				}catch(Exception e){
					e.printStackTrace();
					sendMessageToPlayer("Error occurred when connecting to IRC. Do you have internet access, and access to the IRC server 'irc.esper.net'?", icommandsender);
					return;
				}
				
				sendMessageToPlayer("Bot connected successfully. You may now report bugs using '/sc bug <bug to report>' or contact me using '/sc contact <message>", icommandsender);
			}else if(par1String[0].matches("disconnect")){
				if(mod_SecurityCraft.instance.getIrcBot(icommandsender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(icommandsender.getCommandSenderName()).disconnect();
				}
					
				sendMessageToPlayer("Bot disconnected from EsperNet successfully.", icommandsender);
			}else if(par1String[0].matches("help")){
				this.getCommandSenderAsPlayer(icommandsender).inventory.addItemStackToInventory(new ItemStack(mod_SecurityCraft.scManual));
			}
		}else if(par1String.length >= 2){
			if(par1String[0].matches("bug")){
				if(mod_SecurityCraft.instance.getIrcBot(icommandsender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(icommandsender.getCommandSenderName()).sendMessage("#GeforceMods", "[SecurityCraft " + mod_SecurityCraft.getVersion() + " bug] Geforce: "  + getMessageFromArray(par1String, 1));
					sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + icommandsender.getCommandSenderName() + " --> IRC> " + getMessageFromArray(par1String, 1) + ".", icommandsender);
				}else{
					sendMessageToPlayer("Bot is not connected to EsperNet. Use '/sc connect' to connect to IRC.", icommandsender);
				}
			}else if(par1String[0].matches("contact")){
				if(mod_SecurityCraft.instance.getIrcBot(icommandsender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(icommandsender.getCommandSenderName()).sendMessage("#GeforceMods", "[SecurityCraft " + mod_SecurityCraft.getVersion() + "] Geforce: " + getMessageFromArray(par1String, 1));
					sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + icommandsender.getCommandSenderName() + " --> IRC> " + getMessageFromArray(par1String, 1) + ".", icommandsender);
				}else{
					sendMessageToPlayer("Bot is not connected to EsperNet. Use '/sc connect' to connect to IRC.", icommandsender);
				}
			}
		}else{
			throw new WrongUsageException(usage);
		}
	}

	private static String getMessageFromArray(String[] par1String, int index) {
		String startingString = "";
		for(int i = index; i < par1String.length; i++){
			startingString += (i == index ? "" : " ") + par1String[i];
		}
		
		return startingString;
	}

	private void sendMessageToPlayer(String par1, ICommandSender par2){
		ChatComponentText chatcomponenttext = new ChatComponentText(par1);
		((EntityPlayerMP) getPlayer(par2, par2.getCommandSenderName())).addChatComponentMessage(chatcomponenttext);
	}
	
	public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}
