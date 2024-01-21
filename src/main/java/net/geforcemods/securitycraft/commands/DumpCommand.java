package net.geforcemods.securitycraft.commands;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class DumpCommand {
	private static final DynamicCommandExceptionType ERROR_REGISTRY_NOT_FOUND = new DynamicCommandExceptionType(registry -> new TranslationTextComponent("commands.securitycraft.dump.notFound", registry));
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

	public static ArgumentBuilder<CommandSource, ?> register() {
		//@formatter:off
		return Commands.literal("dump")
				.then(Commands.argument("registry", StringArgumentType.word())
						.suggests((ctx, builder) -> ISuggestionProvider.suggest(REGISTRIES.keySet(), builder))
						.executes(ctx -> { //@formatter:on
							String registry = ctx.getArgument("registry", String.class);

							if (!REGISTRIES.containsKey(registry))
								throw ERROR_REGISTRY_NOT_FOUND.create(registry);

							CommandSource source = ctx.getSource();
							final String lineSeparator = System.lineSeparator();
							final String finalResult;
							final Collection<?> registryObjects = REGISTRIES.get(registry).getEntries();
							String result = "";

							for (Object ro : registryObjects) {
								result += ((RegistryObject<?>) ro).getId().toString() + lineSeparator;
							}

							finalResult = result.substring(0, result.lastIndexOf(lineSeparator));

							if (source.getEntity() instanceof ServerPlayerEntity) {
								ctx.getSource().sendSuccess(new StringTextComponent("[") //@formatter:off
									.append(new StringTextComponent("SecurityCraft").withStyle(TextFormatting.GOLD))
									.append(new StringTextComponent("] "))
									.append(Utils.localize("commands.securitycraft.dump.result", registryObjects.size())
											.withStyle(style -> style
													.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(registry)))
													.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, finalResult)))), true);
								//@formatter:on
							}
							else
								source.sendSuccess(new StringTextComponent(finalResult), true);

							return 0;
						}));
	}
}
