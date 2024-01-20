package net.geforcemods.securitycraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
				.then(DumpCommand.register())
				.then(help())
				.then(OwnerCommand.register());
		//@formatter:on
	}

	private static ArgumentBuilder<CommandSourceStack, ?> bug() {
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayerOrException(), Component.literal("SecurityCraft"), Utils.localize("commands.securitycraft.bug"), SendTip.TIPS_WITH_LINK.get("discord"), ChatFormatting.GOLD);
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> connect() {
		return Commands.literal("connect").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendSystemMessage(Component.literal("[")
					.append(Component.literal("IRC").withStyle(ChatFormatting.GREEN))
					.append(Component.literal("] "))
					.append(Utils.localize("commands.securitycraft.irc.connected"))
					.append(Component.literal(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.TIPS_WITH_LINK.get("discord"))));
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> help() {
		return Commands.literal("help").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendSystemMessage(Component.translatable("commands.securitycraft.help",
					Component.translatable(Blocks.CRAFTING_TABLE.getDescriptionId()),
					Component.translatable(Items.BOOK.getDescriptionId()),
					Component.translatable(Items.IRON_BARS.getDescriptionId())));
			//@formatter:on
			return 0;
		});
	}
}
