package net.geforcemods.securitycraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

public class SCCommand {
	private SCCommand() {}

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(literal("sc"));
		dispatcher.register(literal("securitycraft"));
	}

	public static LiteralArgumentBuilder<CommandSource> literal(String literal) {
		//@formatter:off
		return LiteralArgumentBuilder.<CommandSource>literal(literal)
				.then(bug())
				.then(connect())
				.then(ConvertCommand.register())
				.then(DumpCommand.register())
				.then(help())
				.then(OwnerCommand.register());
		//@formatter:on
	}

	private static ArgumentBuilder<CommandSource, ?> bug() {
		return Commands.literal("bug").executes(ctx -> {
			//@formatter:off
			ctx.getSource().sendSuccess(new StringTextComponent("[")
					.append(new StringTextComponent("SecurityCraft").withStyle(TextFormatting.GOLD))
					.append(new StringTextComponent("] ")).withStyle(TextFormatting.WHITE)
					.append(Utils.localize("commands.securitycraft.bug"))
					.append(new StringTextComponent(": "))
					.append(ForgeHooks.newChatWithLinks(SendTip.TIPS_WITH_LINK.get("discord"))), true);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> connect() {
		return Commands.literal("connect").executes(ctx -> {
			//@formatter:off
			ctx.getSource().sendSuccess(new StringTextComponent("[")
					.append(new StringTextComponent("IRC").withStyle(TextFormatting.GREEN))
					.append(new StringTextComponent("] "))
					.append(Utils.localize("commands.securitycraft.irc.connected"))
					.append(new StringTextComponent(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.TIPS_WITH_LINK.get("discord"))), true);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> help() {
		return Commands.literal("help").executes(ctx -> {
			//@formatter:off
			ctx.getSource().sendSuccess(new TranslationTextComponent("commands.securitycraft.help",
					new TranslationTextComponent(Blocks.CRAFTING_TABLE.getDescriptionId()),
					new TranslationTextComponent(Items.BOOK.getDescriptionId()),
					new TranslationTextComponent(Items.IRON_BARS.getDescriptionId())), true);
			//@formatter:on
			return 0;
		});
	}
}
