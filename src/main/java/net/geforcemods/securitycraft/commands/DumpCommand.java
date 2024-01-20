package net.geforcemods.securitycraft.commands;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DumpCommand {
	private static final DynamicCommandExceptionType ERROR_REGISTRY_NOT_FOUND = new DynamicCommandExceptionType(registry -> Component.translatableWithFallback("commands.securitycraft.dump.notFound", "SecurityCraft has nothing registered to \"%s\".", registry));
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

	private DumpCommand() {}

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		//@formatter:off
		return Commands.literal("dump")
				.then(Commands.argument("registry", StringArgumentType.word())
						.suggests((ctx, builder) -> SharedSuggestionProvider.suggest(REGISTRIES.keySet(), builder))
						.executes(ctx -> { //@formatter:on
							String registry = ctx.getArgument("registry", String.class);

							if (!REGISTRIES.containsKey(registry))
								throw ERROR_REGISTRY_NOT_FOUND.create(registry);

							CommandSourceStack source = ctx.getSource();
							final String lineSeparator = System.lineSeparator();
							final String finalResult;
							final var registryObjects = REGISTRIES.get(registry).getEntries();
							String result = "";

							for (RegistryObject<?> ro : registryObjects) {
								result += ro.getId().toString() + lineSeparator;
							}

							finalResult = result.substring(0, result.lastIndexOf(lineSeparator));

							if (source.isPlayer()) {
								source.getPlayer().sendSystemMessage(Component.literal("[") //@formatter:off
									.append(Component.literal("SecurityCraft").withStyle(ChatFormatting.GOLD))
									.append(Component.literal("] "))
									.append(Component.translatable("commands.securitycraft.dump.result", registryObjects.size())
											.withStyle(style -> style
													.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(registry)))
													.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, finalResult)))));
								//@formatter:on
							}
							else
								source.source.sendSystemMessage(Component.literal(finalResult));

							return 0;
						}));
	}
}
