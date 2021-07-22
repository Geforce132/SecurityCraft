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
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

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
			ctx.getSource().getPlayerOrException().sendMessage(new StringTextComponent("[")
					.append(new StringTextComponent("IRC").withStyle(TextFormatting.GREEN))
					.append(new StringTextComponent("] "))
					.append(Utils.localize("messages.securitycraft:irc.connected"))
					.append(new StringTextComponent(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.tipsWithLink.get("discord"))), Util.NIL_UUID); //appendSibling
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> help()
	{
		return Commands.literal("help").executes(ctx -> {
			ctx.getSource().getPlayerOrException().sendMessage(new TranslationTextComponent("messages.securitycraft:sc_help",
					new TranslationTextComponent(Blocks.CRAFTING_TABLE.getDescriptionId()),
					new TranslationTextComponent(Items.BOOK.getDescriptionId()),
					new TranslationTextComponent(Items.IRON_BARS.getDescriptionId())), Util.NIL_UUID);
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> bug()
	{
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayerOrException(), new StringTextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:bugReport"), SendTip.tipsWithLink.get("discord"), TextFormatting.GOLD);
			return 0;
		});
	}
}
