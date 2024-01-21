package net.geforcemods.securitycraft.commands;

import java.util.Collection;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.GameProfileArgument;

public class SingleGameProfileArgument extends GameProfileArgument {
	public static SingleGameProfileArgument singleGameProfile() {
		return new SingleGameProfileArgument();
	}

	public static GameProfile getGameProfile(CommandContext<CommandSource> ctx, String name) throws CommandSyntaxException {
		return getGameProfiles(ctx, name).iterator().next();
	}

	@Override
	public GameProfileArgument.IProfileProvider parse(StringReader reader) throws CommandSyntaxException {
		GameProfileArgument.IProfileProvider result = super.parse(reader);

		if (result instanceof ProfileProvider) {
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
