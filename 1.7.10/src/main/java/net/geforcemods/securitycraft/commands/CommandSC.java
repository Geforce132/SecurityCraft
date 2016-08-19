package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    
	public String getCommandName() {
		return "sc";
	}
	
	public List<String> getCommandAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender icommandsender) {
		return StatCollector.translateToLocal("messages.command.sc.usage");
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	public void processCommand(ICommandSender sender, String[] par1String) {
		if(par1String.length == 0){
			throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		}
		
		if((par1String[0].matches("connect") || par1String[0].matches("disconnect") || par1String[0].matches("contact") || par1String[0].matches("bug")) && !mod_SecurityCraft.configHandler.isIrcBotEnabled){
			PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.botDisabled"), EnumChatFormatting.RED);
			return;
		}
		
		if(par1String.length == 1){
			if(par1String[0].matches("connect")){
				EntityPlayer p = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());
				
				p.openGui(mod_SecurityCraft.instance, GuiHandler.IRC_INFORMATION, p.worldObj, p.chunkCoordX, p.chunkCoordY, p.chunkCoordZ);
				
				try{
					mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).connectToChannel();
				}catch(Exception e){
					e.printStackTrace();
					PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.error"), EnumChatFormatting.RED);
					return;
				}
				
				PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.connected"), EnumChatFormatting.GREEN);
			}else if(par1String[0].matches("disconnect")){
				if(mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).disconnect();
				}
					
				PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.disconnected"), EnumChatFormatting.RED);
			}else if(par1String[0].matches("help")){
				getCommandSenderAsPlayer(sender).inventory.addItemStackToInventory(new ItemStack(mod_SecurityCraft.scManual));
			}
			else if(par1String[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
			else if(par1String[0].equals("resume"))
				mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).setMessageMode(false, sender);
			else if(par1String[0].matches("contact")){
				if(mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).setMessageMode(true, sender);
				}else{
					PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.notConnected"), EnumChatFormatting.RED);
				}
			}
		}else if(par1String.length >= 2){
			if(par1String[0].matches("contact")){
				if(mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).setMessageMode(true, sender);
				}else{
					PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.notConnected"), EnumChatFormatting.RED);
				}
			}
			else if(par1String[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
			else if(par1String[0].equals("resume"))
				mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).setMessageMode(false, sender);
		}else{
			throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		}
	}

//	private static String getMessageFromArray(String[] par1String, int index) {
//		String startingString = "";
//		for(int i = index; i < par1String.length; i++){
//			startingString += (i == index ? "" : " ") + par1String[i];
//		}
//		
//		return startingString;
//	}
//
//	private void sendMessageToPlayer(String par1, ICommandSender par2){
//		ChatComponentText chatcomponenttext = new ChatComponentText(par1);
//		getPlayer(par2, par2.getCommandSenderName()).addChatComponentMessage(chatcomponenttext);
//	}
	
	public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}
