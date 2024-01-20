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

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

public class LowercasedEnumArgument<T extends Enum<T>> implements ArgumentType<T> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> new TranslatableComponent("commands.forge.arguments.enum.invalid", constants, found));
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

	public static class Serializer implements ArgumentSerializer<LowercasedEnumArgument<?>> {
		@Override
		public void serializeToNetwork(LowercasedEnumArgument<?> argument, FriendlyByteBuf buffer) {
			buffer.writeUtf(argument.enumClass.getName());
		}

		@SuppressWarnings("rawtypes")
		@Override
		public LowercasedEnumArgument<?> deserializeFromNetwork(FriendlyByteBuf buffer) {
			try {
				return new LowercasedEnumArgument(Class.forName(buffer.readUtf()));
			}
			catch (ClassNotFoundException e) {
				return null;
			}
		}

		@Override
		public void serializeToJson(LowercasedEnumArgument<?> argument, JsonObject json) {
			json.addProperty("enum", argument.enumClass.getName());
		}
	}
}
