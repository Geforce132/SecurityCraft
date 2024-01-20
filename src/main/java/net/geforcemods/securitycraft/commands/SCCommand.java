package net.geforcemods.securitycraft.commands;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeHooks;

public class SCCommand {
	private SCCommand() {}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("sc"));
		dispatcher.register(literal("securitycraft"));
	}

	public static LiteralArgumentBuilder<CommandSourceStack> literal(String literal) {
		//@formatter:off
		return LiteralArgumentBuilder.<CommandSourceStack>literal(literal)
				.requires(Predicates.alwaysTrue())
				.then(bug())
				.then(connect())
				.then(DumpCommand.register())
				.then(help())
				.then(OwnerCommand.register());
		//@formatter:on
	}

	private static ArgumentBuilder<CommandSourceStack, ?> bug() {
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayerOrException(), new TextComponent("SecurityCraft"), Utils.localize("commands.securitycraft.bug"), SendTip.TIPS_WITH_LINK.get("discord"), ChatFormatting.GOLD);
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> connect() {
		return Commands.literal("connect").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendMessage(new TextComponent("[")
					.append(new TextComponent("IRC").withStyle(ChatFormatting.GREEN))
					.append(new TextComponent("] "))
					.append(Utils.localize("commands.securitycraft.irc.connected"))
					.append(new TextComponent(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.TIPS_WITH_LINK.get("discord"))), Util.NIL_UUID);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> help() {
		return Commands.literal("help").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendMessage(new TranslatableComponent("commands.securitycraft.help",
					new TranslatableComponent(Blocks.CRAFTING_TABLE.getDescriptionId()),
					new TranslatableComponent(Items.BOOK.getDescriptionId()),
					new TranslatableComponent(Items.IRON_BARS.getDescriptionId())), Util.NIL_UUID);
			//@formatter:on
			return 0;
		});
	}
}
