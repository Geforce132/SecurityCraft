package net.geforcemods.securitycraft.commands;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class SCCommand {
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
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().asPlayer(), new StringTextComponent("IRC"), Utils.localize("messages.securitycraft:irc.connected"), SendTip.tipsWithLink.get("discord"), TextFormatting.GREEN);
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> help()
	{
		return Commands.literal("help").executes(ctx -> {
			ctx.getSource().asPlayer().sendMessage(new TranslationTextComponent("messages.securitycraft:sc_help",
					new TranslationTextComponent(Blocks.CRAFTING_TABLE.getTranslationKey()),
					new TranslationTextComponent(Items.BOOK.getTranslationKey()),
					new TranslationTextComponent(Items.IRON_BARS.getTranslationKey())));
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> bug()
	{
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().asPlayer(), new StringTextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:bugReport"), SendTip.tipsWithLink.get("discord"), TextFormatting.GOLD);
			return 0;
		});
	}
}
