package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;

public class CommandSC extends CommandBase implements ICommand{

	private List<String> nicknames;

	public CommandSC(){
		nicknames = new ArrayList<>();
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
	public String getName() {
		return "sc";
	}

	@Override
	public List<String> getAliases() {
		return nicknames;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return ClientUtils.localize("messages.securitycraft:command.sc.usage");
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0)
			throw new WrongUsageException(ClientUtils.localize("messages.securitycraft:command.sc.usage"));
		else if(args.length == 1){
			if(args[0].equals("connect"))
				sender.sendMessage(new TextComponentString("[" + TextFormatting.GREEN + "IRC" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:irc.connected") + " ").appendSibling(ForgeHooks.newChatWithLinks(SCEventHandler.tipsWithLink.get("discord"))));
			else if(args[0].equals("help")) {
				sender.sendMessage(new TextComponentTranslation("messages.securitycraft:sc_help",
						new TextComponentTranslation(Blocks.CRAFTING_TABLE.getTranslationKey() + ".name"),
						new TextComponentTranslation(Items.BOOK.getTranslationKey() + ".name"),
						new TextComponentTranslation(Blocks.IRON_BARS.getTranslationKey() + ".name")));
			} else if(args[0].equals("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", ClientUtils.localize("messages.securitycraft:bugReport"), "https://discord.gg/U8DvBAW", TextFormatting.GOLD);
			else
				throw new WrongUsageException(ClientUtils.localize("messages.securitycraft:command.sc.usage"));
		}else if(args.length >= 2){
			if(args[0].equals("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", ClientUtils.localize("messages.securitycraft:bugReport"), "https://discord.gg/U8DvBAW", TextFormatting.GOLD);
			else
				throw new WrongUsageException(ClientUtils.localize("messages.securitycraft:command.sc.usage"));
		}
		else
			throw new WrongUsageException(ClientUtils.localize("messages.securitycraft:command.sc.usage"));
	}
}
