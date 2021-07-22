package net.geforcemods.securitycraft.commands;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.Items;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeHooks;

public class SCCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("sc")
				.requires(Predicates.alwaysTrue())
				.then(connect())
				.then(help())
				.then(bug()));
	}

	private static ArgumentBuilder<CommandSourceStack, ?> connect()
	{
		return Commands.literal("connect").executes(ctx -> {
			ctx.getSource().getPlayerOrException().sendMessage(new TextComponent("[")
					.append(new TextComponent("IRC").withStyle(ChatFormatting.GREEN))
					.append(new TextComponent("] "))
					.append(Utils.localize("messages.securitycraft:irc.connected"))
					.append(new TextComponent(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.tipsWithLink.get("discord"))), Util.NIL_UUID); //appendSibling
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> help()
	{
		return Commands.literal("help").executes(ctx -> {
			ctx.getSource().getPlayerOrException().sendMessage(new TranslatableComponent("messages.securitycraft:sc_help",
					new TranslatableComponent(Blocks.CRAFTING_TABLE.getDescriptionId()),
					new TranslatableComponent(Items.BOOK.getDescriptionId()),
					new TranslatableComponent(Items.IRON_BARS.getDescriptionId())), Util.NIL_UUID);
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> bug()
	{
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayerOrException(), new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:bugReport"), SendTip.tipsWithLink.get("discord"), ChatFormatting.GOLD);
			return 0;
		});
	}
}
