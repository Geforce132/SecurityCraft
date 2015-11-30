package net.geforcemods.securitycraft.commands;

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
	
	/**
     * Return the required permission level for this command.
     */
	@Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    
    @Override
	public String getCommandName()
    {
		return "sc";
	}
	
    @Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return StatCollector.translateToLocal("messages.command.sc.usage");
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		if(args.length == 0){
			throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		}
		
		if((args[0].matches("connect") || args[0].matches("disconnect") || args[0].matches("contact") || args[0].matches("bug")) && !mod_SecurityCraft.configHandler.isIrcBotEnabled){
			PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.botDisabled"), EnumChatFormatting.RED);
			return;
		}

		if(args.length == 1){
			if(args[0].matches("connect")){
								
				try{
					mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).connectToChannel();
				}catch(Exception e){
					e.printStackTrace();
					PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.error"), EnumChatFormatting.RED);
					return;
				}
				
				PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.connected"), EnumChatFormatting.GREEN);
				PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.info"), EnumChatFormatting.GREEN);
			}else if(args[0].matches("disconnect")){
				if(mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).disconnect();
				}
					
				PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.disconnected"), EnumChatFormatting.RED);
			}else if(args[0].matches("help")){
				getCommandSenderAsPlayer(sender).inventory.addItemStackToInventory(new ItemStack(mod_SecurityCraft.scManual));
			}
			else if(args[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
		}else if(args.length >= 2){
			if(args[0].matches("contact")){
				if(mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()) != null){
					mod_SecurityCraft.instance.getIrcBot(sender.getCommandSenderName()).sendMessage("#GeforceMods", "> " + getMessageFromArray(args, 1));
					sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + sender.getCommandSenderName() + " --> IRC> " + getMessageFromArray(args, 1), sender);
				}else{
					PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.notConnected"), EnumChatFormatting.RED);
				}
			}
			else if(args[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
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
		((EntityPlayerMP) getPlayer(par2, par2.getCommandSenderName())).addChatComponentMessage(chatcomponenttext);
	}
}
