package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeHooks;

public class CommandSC extends CommandBase implements ICommand{

	private List<String> nicknames;

	public CommandSC(){
		nicknames = new ArrayList<String>();
		nicknames.add("sc");
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getCommandName() {
		return "sc";
	}

	@Override
	public List<String> getCommandAliases() {
		return nicknames;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return StatCollector.translateToLocal("messages.command.sc.usage");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] par1String) {
		if(par1String.length == 0)
			throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		else if(par1String.length == 1){
			if(par1String[0].matches("connect"))
				sender.addChatMessage(new ChatComponentText("[" + EnumChatFormatting.GREEN + "IRC" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.irc.connected") + " ").appendSibling(ForgeHooks.newChatWithLinks(SCEventHandler.tipsWithLink.get("discord"))));
			else if(par1String[0].matches("help"))
				getCommandSenderAsPlayer(sender).inventory.addItemStackToInventory(new ItemStack(SCContent.scManual));
			else if(par1String[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
			else
				throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		}else if(par1String.length >= 2){
			if(par1String[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
			else
				throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
		}
		else
			throw new WrongUsageException(StatCollector.translateToLocal("messages.command.sc.usage"));
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

	@Override
	public int compareTo(Object par1Obj)
	{
		return this.compareTo((ICommand)par1Obj);
	}
}
