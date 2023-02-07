package net.geforcemods.securitycraft.commands;

import java.lang.reflect.Field;
import java.util.Collection;
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
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

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

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("sc")
		//@formatter:off
				.requires(Predicates.alwaysTrue())
				.then(dump())
				.then(connect())
				.then(help())
				.then(bug()));
		//@formatter:on
	}

	private static ArgumentBuilder<CommandSource, ?> dump() {
		//@formatter:off
		return Commands.literal("dump")
				.then(Commands.argument("registry", StringArgumentType.word())
						.suggests((ctx, builder) -> ISuggestionProvider.suggest(REGISTRIES.keySet(), builder))
						.executes(ctx -> { //@formatter:on
							String registry = ctx.getArgument("registry", String.class);

							if (!REGISTRIES.containsKey(registry))
								throw new CommandException(Utils.localize("messages.securitycraft:dump.notFound", registry));

							final String lineSeparator = System.lineSeparator();
							final String finalResult;
							final Collection<?> registryObjects = REGISTRIES.get(registry).getEntries();
							String result = "";

							for (Object ro : registryObjects) {
								result += ((RegistryObject<?>) ro).getId().toString() + lineSeparator;
							}

							finalResult = result;
							ctx.getSource().getPlayerOrException().sendMessage(new StringTextComponent("[") //@formatter:off
									.append(new StringTextComponent("SecurityCraft").withStyle(TextFormatting.GOLD))
									.append(new StringTextComponent("] "))
									.append(Utils.localize("messages.securitycraft:dump.result", registryObjects.size())
											.withStyle(style -> style
													.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(registry)))
													.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, finalResult.substring(0, finalResult.lastIndexOf(lineSeparator)))))), Util.NIL_UUID);
							//@formatter:on
							return 0;
						}));
	}

	private static ArgumentBuilder<CommandSource, ?> connect() {
		return Commands.literal("connect").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendMessage(new StringTextComponent("[")
					.append(new StringTextComponent("IRC").withStyle(TextFormatting.GREEN))
					.append(new StringTextComponent("] "))
					.append(Utils.localize("messages.securitycraft:irc.connected"))
					.append(new StringTextComponent(" "))
					.append(ForgeHooks.newChatWithLinks(SendTip.tipsWithLink.get("discord"))), Util.NIL_UUID); //appendSibling
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> help() {
		return Commands.literal("help").executes(ctx -> {
			//@formatter:off
			ctx.getSource().getPlayerOrException().sendMessage(new TranslationTextComponent("messages.securitycraft:sc_help",
					new TranslationTextComponent(Blocks.CRAFTING_TABLE.getDescriptionId()),
					new TranslationTextComponent(Items.BOOK.getDescriptionId()),
					new TranslationTextComponent(Items.IRON_BARS.getDescriptionId())), Util.NIL_UUID);
			//@formatter:on
			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> bug() {
		return Commands.literal("bug").executes(ctx -> {
			PlayerUtils.sendMessageEndingWithLink(ctx.getSource().getPlayerOrException(), new StringTextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:bugReport"), SendTip.tipsWithLink.get("discord"), TextFormatting.GOLD);
			return 0;
		});
	}
}
