package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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
		return ClientUtils.localize("messages.command.sc.usage");
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender icommandsender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0)
			throw new WrongUsageException(ClientUtils.localize("messages.command.sc.usage"));
		else if(args.length == 1){
			if(args[0].matches("connect"))
				sender.addChatMessage(new TextComponentString("[" + TextFormatting.GREEN + "IRC" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.irc.connected") + " ").appendSibling(ForgeHooks.newChatWithLinks(SCEventHandler.tipsWithLink.get("discord"))));
			else if(args[0].matches("help"))
				getCommandSenderAsPlayer(sender).inventory.addItemStackToInventory(new ItemStack(SCContent.scManual));
			else if(args[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", ClientUtils.localize("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", TextFormatting.GOLD);
			else
				throw new WrongUsageException(ClientUtils.localize("messages.command.sc.usage"));
		}else if(args.length >= 2){
			if(args[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", ClientUtils.localize("messages.bugReport"), "http://goo.gl/forms/kfRpvvQzfl", TextFormatting.GOLD);
			else
				throw new WrongUsageException(ClientUtils.localize("messages.command.sc.usage"));
		}
		else
			throw new WrongUsageException(ClientUtils.localize("messages.command.sc.usage"));
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
	//	private void sendMessageToPlayer(String par1, ICommandSender par2) throws PlayerNotFoundException{
	//		ChatComponentText chatcomponenttext = new ChatComponentText(par1);
	//		getPlayer(par2, par2.getName()).addChatComponentMessage(chatcomponenttext);
	//	}
}
