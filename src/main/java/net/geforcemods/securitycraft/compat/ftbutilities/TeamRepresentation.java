package net.geforcemods.securitycraft.compat.ftbutilities;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

public class TeamRepresentation {
	private final String name;
	private final TextFormatting color;

	public TeamRepresentation(String name, TextFormatting color) {
		this.name = name;
		this.color = color;
	}

	public String name() {
		return name;
	}

	public TextFormatting color() {
		return color;
	}

	public static TeamRepresentation get(Owner owner) {
		if (Loader.isModLoaded("ftbutilities"))
			return FTBUtilitiesCompat.getTeamRepresentation(owner);

		ScorePlayerTeam team = PlayerUtils.getPlayersVanillaTeam(owner.getName());

		if (team != null) {
			TextFormatting color = team.getColor();

			return new TeamRepresentation(team.getDisplayName(), !color.isColor() ? TextFormatting.GRAY : color);
		}

		return null;
	}
}
