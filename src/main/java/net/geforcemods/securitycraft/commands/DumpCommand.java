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
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DumpCommand {
	private static final DynamicCommandExceptionType ERROR_REGISTRY_NOT_FOUND = new DynamicCommandExceptionType(registry -> new TranslatableComponent("commands.securitycraft.dump.notFound", registry));
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

							if (source.getEntity() instanceof ServerPlayer) {
								source.sendSuccess(new TextComponent("[") //@formatter:off
									.append(new TextComponent("SecurityCraft").withStyle(ChatFormatting.GOLD))
									.append(new TextComponent("] "))
									.append(new TranslatableComponent("commands.securitycraft.dump.result", registryObjects.size())
											.withStyle(style -> style
													.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(registry)))
													.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, finalResult)))), true);
								//@formatter:on
							}
							else
								source.sendSuccess(new TextComponent(finalResult), true);

							return 0;
						}));
	}
}
