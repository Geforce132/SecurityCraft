package net.geforcemods.securitycraft.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class LowercasedEnumArgument<T extends Enum<T>> implements ArgumentType<T> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> Component.translatable("commands.neoforge.arguments.enum.invalid", constants, found));
	private final Class<T> enumClass;

	public static <R extends Enum<R>> LowercasedEnumArgument<R> enumArgument(Class<R> enumClass) {
		return new LowercasedEnumArgument<>(enumClass);
	}

	private LowercasedEnumArgument(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public T parse(final StringReader reader) throws CommandSyntaxException {
		String name = reader.readUnquotedString();

		try {
			return Enum.valueOf(enumClass, name.toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException e) {
			throw INVALID_ENUM.createWithContext(reader, name.toLowerCase(Locale.ENGLISH), Arrays.toString(Arrays.stream(enumClass.getEnumConstants()).map(this::toLowercasedEnumName).toArray()));
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(Stream.of(enumClass.getEnumConstants()).map(this::toLowercasedEnumName), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return Stream.of(enumClass.getEnumConstants()).map(this::toLowercasedEnumName).toList();
	}

	private String toLowercasedEnumName(Enum<?> theEnum) {
		return theEnum.name().toLowerCase(Locale.ENGLISH);
	}

	public static class Info<T extends Enum<T>> implements ArgumentTypeInfo<LowercasedEnumArgument<T>, Info<T>.Template> {
		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			buffer.writeUtf(template.enumClass.getName());
		}

		@SuppressWarnings("unchecked")
		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			try {
				return new Template((Class<T>) Class.forName(buffer.readUtf()));
			}
			catch (ClassNotFoundException e) {
				return null;
			}
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			json.addProperty("enum", template.enumClass.getName());
		}

		@Override
		public Template unpack(LowercasedEnumArgument<T> argument) {
			return new Template(argument.enumClass);
		}

		public class Template implements ArgumentTypeInfo.Template<LowercasedEnumArgument<T>> {
			final Class<T> enumClass;

			Template(Class<T> enumClass) {
				this.enumClass = enumClass;
			}

			@Override
			public LowercasedEnumArgument<T> instantiate(CommandBuildContext ctx) {
				return new LowercasedEnumArgument<>(enumClass);
			}

			@Override
			public ArgumentTypeInfo<LowercasedEnumArgument<T>, ?> type() {
				return Info.this;
			}
		}
	}
}
