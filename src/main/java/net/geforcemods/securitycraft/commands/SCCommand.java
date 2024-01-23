package net.geforcemods.securitycraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Style;
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
				.then(bug())
				.then(connect())
				.then(ConvertCommand.register())
				.then(DumpCommand.register())
				.then(help())
				.then(OwnerCommand.register());
		//@formatter:on
	}

	private static ArgumentBuilder<CommandSourceStack, ?> bug() {
		return Commands.literal("bug").executes(ctx -> {
			//@formatter:off
			ctx.getSource().sendSuccess(new TextComponent("[")
					.append(new TextComponent("SecurityCraft").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)))
					.append(new TextComponent("] ")).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
					.append(Utils.localize("commands.securitycraft.bug"))
					.append(new TextComponent(": "))
					.append(ForgeHooks.newChatWithLinks(SendTip.TIPS_WITH_LINK.get("discord"))), true);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> connect() {
		return Commands.literal("connect").executes(ctx -> {
			//@formatter:off
			ctx.getSource().sendSuccess(new TextComponent("[")
					.append(new TextComponent("IRC").withStyle(ChatFormatting.GREEN))
					.append(new TextComponent("] "))
					.append(Utils.localize("commands.securitycraft.connect"))
					.append(new TextComponent(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.TIPS_WITH_LINK.get("discord"))), true);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> help() {
		return Commands.literal("help").executes(ctx -> {
			//@formatter:off
			ctx.getSource().sendSuccess(new TranslatableComponent("commands.securitycraft.help",
					new TranslatableComponent(Blocks.CRAFTING_TABLE.getDescriptionId()),
					new TranslatableComponent(Items.BOOK.getDescriptionId()),
					new TranslatableComponent(Items.IRON_BARS.getDescriptionId())), true);
			//@formatter:on
			return 0;
		});
	}
}
