package net.geforcemods.securitycraft.commands;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;

public class CommandSC {
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("sc")
				.requires(Predicates.alwaysTrue())
				.then(connect())
				.then(help())
				.then(bug()));
	}

	private static ArgumentBuilder<CommandSource, ?> connect()
	{
		return Commands.literal("connect").executes(ctx -> {
			ctx.getSource().asPlayer().sendMessage(new StringTextComponent("[" + TextFormatting.GREEN + "IRC" + TextFormatting.WHITE + "] " + ClientUtils.localize("messages.securitycraft:irc.connected") + " ").appendSibling(ForgeHooks.newChatWithLinks(SCEventHandler.tipsWithLink.get("discord"))));
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> help()
	{
		return Commands.literal("help").executes(ctx -> {
			ctx.getSource().asPlayer().addItemStackToInventory(new ItemStack(SCContent.scManual));
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> bug()
	{
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().asPlayer(), "SecurityCraft", ClientUtils.localize("messages.securitycraft:bugReport"), "http://goo.gl/forms/kfRpvvQzfl", TextFormatting.GOLD);
			return 0;
		});
	}
}
