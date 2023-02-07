package net.geforcemods.securitycraft.commands;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.network.client.SendTip;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SCCommand {
	private static final Map<String, DeferredRegister<?>> REGISTRIES = Util.make(() -> {
		Map<String, DeferredRegister<?>> map = new Object2ObjectArrayMap<>();

		for (Field field : SCContent.class.getFields()) {
			if (field.getType() != DeferredRegister.class)
				return map;

			try {
				map.put(field.getName().toLowerCase(Locale.ROOT), (DeferredRegister<?>) field.get(null));
			}
			catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return map;
	});

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("sc")
		//@formatter:off
				.requires(Predicates.alwaysTrue())
				.then(dump())
				.then(connect())
				.then(help())
				.then(bug()));
		//@formatter:on
	}

	private static ArgumentBuilder<CommandSourceStack, ?> dump() {
		//@formatter:off
		return Commands.literal("dump")
				.then(Commands.argument("registry", StringArgumentType.word())
						.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(REGISTRIES.keySet(), builder))
						.executes(ctx -> { //@formatter:on
							String registry = ctx.getArgument("registry", String.class);

							if (!REGISTRIES.containsKey(registry))
								throw new CommandRuntimeException(new TranslatableComponent("messages.securitycraft:dump.notFound", registry));

							final String lineSeparator = System.lineSeparator();
							final String finalResult;
							final var registryObjects = REGISTRIES.get(registry).getEntries();
							String result = "";

							for (RegistryObject<?> ro : registryObjects) {
								result += ro.getId().toString() + lineSeparator;
							}

							finalResult = result;
							ctx.getSource().getPlayerOrException().sendMessage(new TextComponent("[") //@formatter:off
									.append(new TextComponent("SecurityCraft").withStyle(ChatFormatting.GOLD))
									.append(new TextComponent("] "))
									.append(new TranslatableComponent("messages.securitycraft:dump.result", registryObjects.size())
											.withStyle(style -> style
													.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(registry)))
													.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, finalResult.substring(0, finalResult.lastIndexOf(lineSeparator)))))), Util.NIL_UUID);
							//@formatter:on
							return 0;
						}));
	}

	private static ArgumentBuilder<CommandSourceStack, ?> connect() {
		return Commands.literal("connect").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendMessage(new TextComponent("[")
					.append(new TextComponent("IRC").withStyle(ChatFormatting.GREEN))
					.append(new TextComponent("] "))
					.append(Utils.localize("messages.securitycraft:irc.connected"))
					.append(new TextComponent(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.tipsWithLink.get("discord"))), Util.NIL_UUID);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> help() {
		return Commands.literal("help").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendMessage(new TranslatableComponent("messages.securitycraft:sc_help",
					new TranslatableComponent(Blocks.CRAFTING_TABLE.getDescriptionId()),
					new TranslatableComponent(Items.BOOK.getDescriptionId()),
					new TranslatableComponent(Items.IRON_BARS.getDescriptionId())), Util.NIL_UUID);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSourceStack, ?> bug() {
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayerOrException(), new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:bugReport"), SendTip.tipsWithLink.get("discord"), ChatFormatting.GOLD);
			return 0;
		});
	}
}
