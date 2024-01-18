package net.geforcemods.securitycraft.commands;

import java.util.Collection;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;

public class SingleGameProfileArgument extends GameProfileArgument {
	public static SingleGameProfileArgument singleGameProfile() {
		return new SingleGameProfileArgument();
	}

	public static GameProfile getGameProfile(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
		return getGameProfiles(ctx, name).iterator().next();
	}

	@Override
	public GameProfileArgument.Result parse(StringReader reader) throws CommandSyntaxException {
		GameProfileArgument.Result result = super.parse(reader);

		if (result instanceof SelectorResult) {
			return source -> {
				Collection<GameProfile> profiles = result.getNames(source);

				if (profiles.size() > 1)
					throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
				else
					return profiles;
			};
		}
		else
			return result;
	}
}
