package net.geforcemods.securitycraft.compat.ftbteams;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ModList;

public class TeamRepresentation {
	private final String name;
	private final int color;

	public TeamRepresentation(String name, int color) {
		this.name = name;
		this.color = color;
	}

	public String name() {
		return name;
	}

	public int color() {
		return color;
	}

	public static TeamRepresentation get(Owner owner) {
		if (ModList.get().isLoaded("ftbteams"))
			return FTBTeamsCompat.getTeamRepresentation(owner);

		ScorePlayerTeam team = PlayerUtils.getPlayersVanillaTeam(owner.getName());

		if (team != null) {
			Integer color = team.getColor().getColor();

			return new TeamRepresentation(team.getDisplayName().getString(), color == null ? TextFormatting.GRAY.getColor() : color);
		}

		return null;
	}
}
