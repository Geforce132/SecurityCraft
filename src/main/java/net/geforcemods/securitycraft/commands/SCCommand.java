package net.geforcemods.securitycraft.commands;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.server.command.CommandTreeBase;

public class SCCommand extends CommandTreeBase {
	public SCCommand() {
		addSubcommand(new BugCommand());
		addSubcommand(new ConnectCommand());
		addSubcommand(new ConvertCommand());
		addSubcommand(new HelpCommand());
		addSubcommand(new OwnerCommand());
	}

	@Override
	public String getName() {
		return "securitycraft";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("sc");
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/securitycraft <bug|connect|help|owner>";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	private static class BugCommand extends CommandBase {
		@Override
		public String getName() {
			return "bug";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/securitycraft bug";
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			//@formatter:off
			sender.sendMessage(new TextComponentString("[")
					.appendSibling(new TextComponentString("SecurityCraft").setStyle(new Style().setColor(TextFormatting.GOLD)))
					.appendSibling(new TextComponentString(TextFormatting.WHITE + "] "))
					.appendSibling(Utils.localize("commands.securitycraft.bug"))
					.appendSibling(new TextComponentString(": "))
					.appendSibling(ForgeHooks.newChatWithLinks("https://discord.gg/U8DvBAW")));
			//@formatter:on
		}
	}

	private static class ConnectCommand extends CommandBase {
		@Override
		public String getName() {
			return "connect";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/securitycraft connect";
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			//@formatter:off
			sender.sendMessage(new TextComponentString("[" + TextFormatting.GREEN + "IRC" + TextFormatting.WHITE + "] ")
					.appendSibling(Utils.localize("commands.securitycraft.connect"))
					.appendSibling(new TextComponentString(" "))
					.appendSibling(ForgeHooks.newChatWithLinks(SCEventHandler.TIPS_WITH_LINK.get("discord"))));
			//@formatter:on
		}
	}

	private static class HelpCommand extends CommandBase {
		@Override
		public String getName() {
			return "help";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return "/securitycraft help";
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			return true;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			sender.sendMessage(Utils.localize("commands.securitycraft.help", Utils.localize(Blocks.CRAFTING_TABLE), Utils.localize(Items.BOOK), Utils.localize(Blocks.IRON_BARS)));
		}
	}
}
