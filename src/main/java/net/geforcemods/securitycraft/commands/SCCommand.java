package net.geforcemods.securitycraft.commands;

import com.google.common.base.Predicates;
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

//@formatter:off
public class SCCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack> literal("sc")
				.requires(Predicates.alwaysTrue())
				.then(connect())
				.then(help())
				.then(bug()));
	}

	private static ArgumentBuilder<CommandSourceStack, ?> connect() {
		return Commands.literal("connect").executes(ctx -> {
			ctx.getSource().getPlayerOrException().sendSystemMessage(Component.literal("[")
					.append(Component.literal("IRC").withStyle(ChatFormatting.GREEN))
					.append(Component.literal("] "))
					.append(Utils.localize("messages.securitycraft:irc.connected"))
					.append(Component.literal(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.tipsWithLink.get("discord"))));
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> help() {
		return Commands.literal("help").executes(ctx -> {
			ctx.getSource().getPlayerOrException().sendSystemMessage(Component.translatable("messages.securitycraft:sc_help",
					Component.translatable(Blocks.CRAFTING_TABLE.getDescriptionId()),
					Component.translatable(Items.BOOK.getDescriptionId()),
					Component.translatable(Items.IRON_BARS.getDescriptionId())));
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> bug() {
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayerOrException(), Component.literal("SecurityCraft"), Utils.localize("messages.securitycraft:bugReport"), SendTip.tipsWithLink.get("discord"), ChatFormatting.GOLD);
			return 0;
		});
	}
}
